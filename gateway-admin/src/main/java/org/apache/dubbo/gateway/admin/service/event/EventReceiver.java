package org.apache.dubbo.gateway.admin.service.event;


import org.apache.dubbo.gateway.api.model.EventMessage;

import java.util.List;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/10 08:25
 */
public interface EventReceiver {

    void onEvent(String eventKey, List<EventMessage> message);

}
