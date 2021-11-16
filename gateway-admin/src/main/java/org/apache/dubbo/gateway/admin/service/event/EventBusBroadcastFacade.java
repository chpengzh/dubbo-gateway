package org.apache.dubbo.gateway.admin.service.event;


import org.apache.dubbo.gateway.api.model.EventMessage;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 事件广播接口(cluster=broadcast)
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/10 16:08
 */
public interface EventBusBroadcastFacade {

    void onBroadcast(@Nonnull String eventKey, @Nonnull List<EventMessage> message);

}
