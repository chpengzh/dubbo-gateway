package org.apache.dubbo.gateway.admin.service.event.bus;

import org.apache.dubbo.gateway.admin.service.event.EventBus;
import org.apache.dubbo.gateway.admin.service.event.EventBusBroadcastFacade;
import org.apache.dubbo.gateway.admin.service.event.EventReceiver;
import org.apache.dubbo.gateway.api.model.EventMessage;
import org.apache.dubbo.rpc.cluster.support.BroadcastCluster;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 广播事件总线，通过 Dubbo 广播模式驱动, 从一台DC实例广播到所有DC实例
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/10 08:29
 * @see BroadcastCluster
 */
@Component("broadcastEventBus")
public class BroadcastEventBus implements EventBus, EventBusBroadcastFacade {

    private final Map<String, List<EventReceiver>> receivers = new ConcurrentHashMap<>();

    @Lazy
    @Resource(name = "eventBusBroadcastFacade")
    private EventBusBroadcastFacade eventBusBroadcastFacade;

    @Override
    public void register(List<String> eventKeys, EventReceiver receiver) {
        for (String eventKey : eventKeys) {
            receivers.compute(eventKey, (key, collection) -> {
                collection = collection == null
                        ? new CopyOnWriteArrayList<>()
                        : collection;
                collection.add(receiver);
                return collection;
            });
        }
    }

    @Override
    public void publish(String eventKey, List<EventMessage> message) {
        try {
            eventBusBroadcastFacade.onBroadcast(eventKey, message);
        } catch (Throwable ignore) {
        }
    }

    @Override
    public void onBroadcast(@Nonnull String eventKey, @Nonnull List<EventMessage> message) {
        List<EventReceiver> eventReceiver = this.receivers.get(eventKey);
        if (CollectionUtils.isEmpty(eventReceiver)) {
            return;
        }
        for (EventReceiver receiver : new ArrayList<>(eventReceiver)) {
            try {
                receiver.onEvent(eventKey, message);
            } catch (Throwable ignore) {
            }
        }
    }

    @Override
    public void unRegister(List<String> eventKeys, EventReceiver receiver) {
        for (String eventKey : eventKeys) {
            receivers.computeIfPresent(eventKey, (key, collection) -> {
                collection.remove(receiver);
                return collection;
            });
        }
    }
}
