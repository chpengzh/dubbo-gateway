package org.apache.dubbo.gateway.client.event;

import org.apache.dubbo.gateway.api.model.NotifyEventMessage;
import org.apache.dubbo.gateway.api.service.EventMessageService;
import org.apache.dubbo.gateway.client.utils.InnerThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 事件订阅循环工具类
 */
public class EventConsumerLooper {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumerLooper.class);

    private final EventMessageService client;

    private final List<String> eventKeys = new CopyOnWriteArrayList<>();
    private final Map<String, Long> versions = new ConcurrentHashMap<>();
    private final AtomicBoolean initFlag = new AtomicBoolean(false);
    private final ExecutorService looperThread = Executors.newSingleThreadExecutor();
    private volatile EventHandler eventHandler;
    private volatile boolean shutdown = false;

    public EventConsumerLooper(EventMessageClient client) {
        this.client = client;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void setEventKeys(List<String> eventKeys) {
        if (!initFlag.get()) {
            this.eventKeys.clear();
            this.eventKeys.addAll(eventKeys);
        }
    }

    public void setEventHandler(EventHandler eventHandler) {
        if (!initFlag.get()) {
            this.eventHandler = eventHandler;
        }
    }

    @PostConstruct
    public void loop() {
        if (eventKeys.isEmpty()) {
            throw new IllegalArgumentException("缺少 eventKeys.");
        } else if (eventHandler == null) {
            throw new IllegalArgumentException("缺少 eventHandler.");
        }

        if (!initFlag.compareAndSet(false, true)) {
            // 标识循环已经启动
            return;
        }

        Map<String, Long> initVersion = initVersionMap();
        if (initVersion == null) {
            // shutdown场景
            return;
        }
        this.versions.putAll(initVersion);
        looperThread.submit(() -> {
            Map<String, Long> newVersions;
            while (true) {
                if ((newVersions = eventLoop()) != null) {
                    this.versions.putAll(newVersions);
                } else if (!InnerThreadUtils.delay(10_000)) {
                    // 执行中断
                    break;
                }
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        this.shutdown = true;
        looperThread.shutdownNow();
    }

    @Nullable
    private Map<String, Long> initVersionMap() {
        Map<String, Long> initMap = new HashMap<>();
        eventKeys.forEach(key -> initMap.put(key, null));
        while (!shutdown) {
            try {
                NotifyEventMessage message = client.consumeSync(initMap);
                if (message != null) {
                    return message.getEventVersions();
                }
                // 1.消息为空，一般为轮询超时，加一个延时重连
                LOGGER.warn("EventConsumerLooper#init error for empty result, waiting for retry");
                InnerThreadUtils.delay(1_000);
            } catch (Throwable err) {
                LOGGER.warn("EventConsumerLooper#init error for {}, waiting for retry", err.toString());
                // 2.请求错误，一般为网络错误, 加一个延时重连
                InnerThreadUtils.delay(5_000);
            }
        }
        return null;
    }

    private Map<String, Long> eventLoop() {
        Map<String, Long> nextVersion = new HashMap<>(this.versions);
        NotifyEventMessage message;
        try {
            message = client.consumeSync(nextVersion);
            if (message == null) {
                LOGGER.info("GatewayAdmin Event: --heartbeat--");
                return null;
            }
        } catch (Throwable e) {
            LOGGER.error("Unexpected error from EventConsumerLooper#eventLoop#consumeSync", e);
            return null;
        }

        if (!CollectionUtils.isEmpty(message.getMessages())) {
            try {
                LOGGER.info("GatewayAdmin Event: " + message.getMessages());
                eventHandler.onEvent(message.getMessages());
            } catch (Throwable e) {
                LOGGER.error("Unexpected error from EventConsumerLooper#eventLoop#onEvent", e);
            }
        }
        return message.getEventVersions();
    }

    public static class Builder {

        private List<String> eventKeys;

        private EventHandler eventHandler;

        public Builder eventKeys(List<String> eventKeys) {
            this.eventKeys = eventKeys;
            return this;
        }

        public Builder eventHandler(EventHandler eventHandler) {
            this.eventHandler = eventHandler;
            return this;
        }

        public EventConsumerLooper build(EventMessageClient client) {
            EventConsumerLooper looper = new EventConsumerLooper(client);
            looper.setEventKeys(eventKeys);
            looper.setEventHandler(eventHandler);
            return looper;
        }
    }
}