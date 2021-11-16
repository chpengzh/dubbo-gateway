package org.apache.dubbo.gateway.server.repository.sync;

import org.apache.dubbo.gateway.api.constants.EventName;
import org.apache.dubbo.gateway.api.model.EventMessage;
import org.apache.dubbo.gateway.client.event.EventConsumerLooper;
import org.apache.dubbo.gateway.client.event.EventHandler;
import org.apache.dubbo.gateway.client.event.EventMessageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 事件同步方式
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/10 10:55
 */
@Component
@Configuration
@EnableConfigurationProperties(DynamicConfigProperties.class)
public class GatewayEventNotifier implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("notifyLogger");

    @Autowired
    private List<ConfigChangedResolver> eventHandlers;

    public GatewayEventNotifier(DynamicConfigProperties properties) {
        if (!ObjectUtils.isEmpty(properties.getHost())) {
            EventConsumerLooper.builder()
                    .eventHandler(this)
                    .eventKeys(EventName.keys())
                    .build(new EventMessageClient(properties.getHost()))
                    .loop();
        }
    }

    @Override
    public void onEvent(List<EventMessage> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            LOGGER.warn("messages is empty");
            return;
        }
        for (EventMessage message : messages) {
            EventName eventType = EventName.fromKey(message.getEventKey());
            for (ConfigChangedResolver handler : this.eventHandlers) {
                try {
                    handler.resolveChange(eventType, message.getContent(), null);
                } catch (Throwable e) {
                    LOGGER.error("Unexpected error", e);
                }
            }
        }
    }
}
