package org.apache.dubbo.gateway.server.repository.sync;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态配置
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/16 18:41
 */
@Data
@ConfigurationProperties("dynamic.config")
public class DynamicConfigProperties {

    /**
     * 配置 url 地址
     */
    private String host;

}
