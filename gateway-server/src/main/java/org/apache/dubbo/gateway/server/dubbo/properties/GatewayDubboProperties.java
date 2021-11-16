package org.apache.dubbo.gateway.server.dubbo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chpengzh@foxmail.com
 * @date 2020-08-10 10:09
 */
@Data
@ConfigurationProperties("gateway.dubbo")
public class GatewayDubboProperties {

    /**
     * 注册中心地址
     */
    private String registry;

    /**
     * 消费者配置
     */
    private Consumer consumer = new Consumer();

    /**
     * 消费者配置
     */
    @Data
    public static class Consumer {

        /**
         * 检查服务提供者是否存在
         */
        private Boolean check;

        /**
         * 远程调用超时时间(毫秒)
         */
        private Integer timeout;

        /**
         * 服务消费版本号
         */
        private String version;

        /**
         * 消费拦截器
         */
        private String filter;

        /**
         * 负载均衡策略
         */
        private String loadbalance;

        /**
         * 调用重试次数
         */
        private Integer retries;

        /**
         * 选择集群方式
         */
        private String cluster;
    }

}
