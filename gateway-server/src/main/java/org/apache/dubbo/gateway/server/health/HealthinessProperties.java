package org.apache.dubbo.gateway.server.health;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * 健康检查配置
 *
 * @author chpengzh@foxmail.com
 * @date 2020/10/18 17:36
 */
@Data
@Configuration
public class HealthinessProperties {

    /**
     * 是否开启健康检查接口.
     */
    private boolean enable = true;

    /**
     * 进程初始化延迟(s).
     */
    private int initLatencySec = 5;

}
