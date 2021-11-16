package org.apache.dubbo.gateway.client.event;

import org.apache.dubbo.gateway.api.model.EventMessage;

import java.util.List;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/15 11:21
 */
public interface EventHandler {

    void onEvent(List<EventMessage> messages);

}
