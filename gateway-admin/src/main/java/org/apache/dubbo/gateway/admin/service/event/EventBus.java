package org.apache.dubbo.gateway.admin.service.event;


import org.apache.dubbo.gateway.api.model.EventMessage;

import java.util.List;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/10 08:24
 */
public interface EventBus {

    void register(List<String> eventKey, EventReceiver receiver);

    void publish(String eventKey, List<EventMessage> message);

    void unRegister(List<String> eventKey, EventReceiver receiver);

}
