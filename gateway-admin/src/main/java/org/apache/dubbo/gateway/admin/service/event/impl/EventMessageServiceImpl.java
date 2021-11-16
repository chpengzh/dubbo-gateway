package org.apache.dubbo.gateway.admin.service.event.impl;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.gateway.admin.service.event.EventBus;
import org.apache.dubbo.gateway.admin.service.event.EventReceiver;
import org.apache.dubbo.gateway.admin.service.event.EventService;
import org.apache.dubbo.gateway.admin.service.event.properties.EventingProperties;
import org.apache.dubbo.gateway.admin.utils.CopyOnWriteHashMap;
import org.apache.dubbo.gateway.admin.utils.IdempotentBiConsumer;
import io.netty.util.HashedWheelTimer;
import org.apache.dubbo.gateway.api.model.EventMessage;
import org.apache.dubbo.gateway.api.model.NotifyEventMessage;
import org.apache.dubbo.gateway.api.service.EventMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 事件查询实现接口
 * <ul>
 * <li>根据当前版本号进行判断，如果当前版本号低于监听版本，则拉取全量事件返回</li>
 * <li>如果当前版本号为最新，则拉取</li>
 * </ul>
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 13:08
 */
@Configuration
@Service
public class EventMessageServiceImpl implements EventMessageService, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventMessageServiceImpl.class);

    private final HashedWheelTimer timer = new HashedWheelTimer();

    private final EventingProperties properties;

    @Autowired
    private EventService eventingService;

    @Autowired
    private EventBus eventBus;

    public EventMessageServiceImpl(EventingProperties properties) {
        this.properties = properties;
        this.timer.start();
    }

    @Nonnull
    @Override
    public Long produce(@Nonnull String eventKey, @Nonnull List<String> eventContent) {
        return eventingService.publish(eventKey, eventContent);
    }

    @Override
    public void consume(
            @Nonnull Map<String, Long> watchVersions,
            @Nonnull BiConsumer<NotifyEventMessage, Throwable> rec
    ) {
        BiConsumer<NotifyEventMessage, Throwable> boxReceiver = new IdempotentBiConsumer<>(rec);
        EventConsumerContext context = new EventConsumerContext(watchVersions);
        try {
            context.consume(msg -> boxReceiver.accept(msg, null));
        } catch (Throwable err) {
            boxReceiver.accept(null, err);
        }
    }

    @Override
    public void close() {
        this.timer.stop();
    }

    private class EventConsumerContext {

        private static final long NO_VERSION = -1L;

        private final Map<String, Long> watchVersions;

        private final List<String> eventKeys;

        EventConsumerContext(@Nonnull Map<String, Long> watchVersions) {
            this.watchVersions = new CopyOnWriteHashMap<>(watchVersions);
            this.eventKeys = new ArrayList<>(watchVersions.keySet());
        }

        public void consume(Consumer<NotifyEventMessage> consumer) {
            Map<String, Long> currentVersions = eventingService.queryLatestVersion(eventKeys);
            NotifyEventMessage message = syncConsume(currentVersions);
            if (message != null) {
                consumer.accept(message);
            } else {
                asyncConsume(consumer);
            }
        }

        /**
         * 进行版本号比对，全量拉取
         *
         * @param currentVersions 参考当前消息版本号
         * @return {@code null} 如果当前消息版本满足异步监听条件
         */
        @Nullable
        private NotifyEventMessage syncConsume(Map<String, Long> currentVersions) {
            NotifyEventMessage message = new NotifyEventMessage();
            message.setEventVersions(currentVersions);
            message.setMessages(new ArrayList<>());

            boolean syncReturn = false;
            for (Map.Entry<String, Long> watcher : watchVersions.entrySet()) {
                String eventKey = watcher.getKey();
                Long watchVersion = watcher.getValue();
                long curVersion = Optional.ofNullable(currentVersions.get(eventKey)).orElse(NO_VERSION);

                if (watchVersion == null) {
                    // 无版本号，标识首次消费，直接填写回最新版本号
                    currentVersions.put(eventKey, curVersion);
                    syncReturn = true;
                } else if (watchVersion < curVersion) {
                    // 已有版本号, 但是版本号非最新, 直接返回
                    List<EventMessage> syncMessage = eventingService.queryMessages(eventKey,
                            watchVersion, false);
                    long messageMaxVersion = syncMessage.stream()
                            .map(EventMessage::getVersion)
                            .filter(Objects::nonNull)
                            .reduce(Math::max)
                            .orElse(NO_VERSION);
                    long latestVersion = Math.max(messageMaxVersion, curVersion);
                    message.getMessages().addAll(syncMessage);
                    // 设置message中真实的最新版本号, 因为消息版版本可能大于提前获取的参考版版本
                    currentVersions.put(eventKey, latestVersion);
                    syncReturn = true;
                }
            }
            return syncReturn ? message : null;
        }

        /**
         * 注册事件总线，根据事件增量返回
         *
         * @param consumer 事件消费者
         */
        private void asyncConsume(Consumer<NotifyEventMessage> consumer) {
            EventReceiver receiver;
            // 注册事件通知
            eventBus.register(eventKeys, (receiver = new EventReceiver() {
                @Override
                public void onEvent(String eventKey, List<EventMessage> message) {
                    try {
                        onAsyncCallback(eventKey, message, consumer);
                    } finally {
                        // 通知结束断开连接
                        eventBus.unRegister(eventKeys, this);
                    }
                }
            }));
            // 长轮训轮询超时断开连接
            timer.newTimeout(timeout -> {
                try {
                    consumer.accept(null);
                } finally {
                    eventBus.unRegister(eventKeys, receiver);
                }
            }, properties.getPolling(), TimeUnit.SECONDS);
        }

        /**
         * 事件总线回调，当增量事件触发时执行
         *
         * @param notifyKey      监听事件key
         * @param notifyMessages 监听消息体
         * @param consumer       监听事件消费者
         */
        public void onAsyncCallback(
                String notifyKey,
                List<EventMessage> notifyMessages,
                Consumer<NotifyEventMessage> consumer
        ) {
            if (notifyKey == null
                    || CollectionUtils.isEmpty(notifyMessages)) {
                // 这里是不太可能出现的情况, 打印错误日志观察
                LOGGER.error("Illegal notifyKey={} or empty notify message={}",
                        notifyKey,
                        JSON.toJSONString(notifyMessages));
            }

            Long listeningVersion = watchVersions.get(notifyKey);
            Long eventVersion = notifyMessages.get(0).getVersion();
            if (eventVersion <= listeningVersion) {
                // 如果监听版本大于事件版本, 则返回一个空包
                consumer.accept(null);

            } else if (eventVersion == listeningVersion + 1L) {
                watchVersions.put(notifyKey, eventVersion);
                // 如果是递增版本, 则直接返回
                NotifyEventMessage notify = new NotifyEventMessage();
                notify.setEventVersions(new CopyOnWriteHashMap<>(watchVersions));
                notify.setMessages(notifyMessages);
                consumer.accept(notify);

            } else {
                // 如果版本有间隔, 多半为并发错误或者广播失效, 需要降级走一次全量拉取
                consumer.accept(syncConsume(watchVersions));

            }
        }
    }
}
