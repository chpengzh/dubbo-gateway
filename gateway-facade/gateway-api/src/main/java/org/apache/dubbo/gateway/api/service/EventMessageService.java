package org.apache.dubbo.gateway.api.service;

import org.apache.dubbo.gateway.api.constants.EventName;
import org.apache.dubbo.gateway.api.model.NotifyEventMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 网关事件数据源接口
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 13:05
 */
public interface EventMessageService {

    /**
     * 生产事件
     *
     * @param eventKey     事件Key
     * @param eventContent 事件内容
     * @return 发布事件版本号
     */
    @Nonnull
    Long produce(@Nonnull String eventKey, @Nonnull List<String> eventContent);

    /**
     * 生产事件
     *
     * @param eventName    事件Key
     * @param eventContent 事件内容
     * @return 发布事件版本号
     */
    @Nonnull
    default Long produce(@Nonnull EventName eventName, @Nonnull String eventContent) {
        List<String> event = new ArrayList<>();
        event.add(eventContent);
        return produce(eventName.getKey(), event);
    }

    /**
     * 订阅消费事件(长轮询接口)
     *
     * @param watchEvents 监听事件Key, 以及消费起始版本号
     * @param receiver    监听事件结果, 结果为 {@code null} 如果事件监听错误或超过心跳时间
     */
    default void consume(
            @Nonnull Map<String, Long> watchEvents,
            @Nonnull BiConsumer<NotifyEventMessage, Throwable> receiver
    ) {
        throw new UnsupportedOperationException("UnSupported method #consume.");
    }

    /**
     * 同步订阅消费事件(阻塞)
     *
     * @param watchEvents 监听事件key，以及事件起始版本号
     * @return 监听事件结果, 结果为 {@code null} 如果事件监听错误或超过心跳时间
     */
    @Nullable
    default NotifyEventMessage consumeSync(@Nonnull Map<String, Long> watchEvents) throws IOException {
        throw new UnsupportedOperationException("UnSupported method #consumeSync.");
    }
}
