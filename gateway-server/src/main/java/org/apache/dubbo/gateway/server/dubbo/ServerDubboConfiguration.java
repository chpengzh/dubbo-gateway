package org.apache.dubbo.gateway.server.dubbo;

import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.gateway.server.dubbo.factory.RegistryBeanFactoryProcessor;
import org.apache.dubbo.gateway.server.dubbo.properties.GatewayDubboProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 *
 *
 * @author chpengzh@foxmail.com
 * @date 2020-08-10 10:11
 */
@Configuration
@EnableConfigurationProperties(GatewayDubboProperties.class)
public class ServerDubboConfiguration {

    @Bean
    public RegistryBeanFactoryProcessor getRegistryProcessor() {
        return new RegistryBeanFactoryProcessor();
    }

    @Bean("<dubbo:consumer by='ServerDubboConfiguration#getConsumerConfig'>")
    public ConsumerConfig getConsumerConfig(
            ObjectProvider<List<RegistryConfig>> registryProvider,
            GatewayDubboProperties properties
    ) {
        ConsumerConfig config = new ConsumerConfig();
        config.setRegistries(registryProvider.getObject());
        config.setCheck(properties.getConsumer().getCheck());
        config.setTimeout(properties.getConsumer().getTimeout());
        config.setVersion(properties.getConsumer().getVersion());
        config.setFilter(properties.getConsumer().getFilter());
        if (!ObjectUtils.isEmpty(properties.getConsumer().getCluster())) {
            config.setCluster(properties.getConsumer().getCluster());
        }
        config.setLoadbalance(properties.getConsumer().getLoadbalance());
        config.setRetries(properties.getConsumer().getRetries());
        return config;
    }
}
