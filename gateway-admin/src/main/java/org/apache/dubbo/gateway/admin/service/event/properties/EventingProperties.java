package org.apache.dubbo.gateway.admin.service.event.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gateway.data.event")
public class EventingProperties {

    /**
     * 监听长轮询时间(s).
     */
    private Long polling = 60L;

    /**
     * 最大Watcher连接数
     */
    private int maxWatcher = 300;

}