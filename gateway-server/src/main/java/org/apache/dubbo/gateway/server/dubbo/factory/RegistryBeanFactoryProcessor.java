package org.apache.dubbo.gateway.server.dubbo.factory;

import org.apache.dubbo.config.RegistryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 注册中心Bean工厂，支持多注册中心的订阅
 *
 * @author chpengzh@foxmail.com
 * @date 2020-08-10 10:14
 */
public class RegistryBeanFactoryProcessor implements BeanFactoryPostProcessor, EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryBeanFactoryProcessor.class);

    private static final String ZOOKEEPER_REGISTRY = "gateway.dubbo.registry";

    private ConfigurableEnvironment env;

    @Override
    public void postProcessBeanFactory(@Nonnull ConfigurableListableBeanFactory factory) throws BeansException {
        List<String> registries = parseRegistries(env);

        final BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) factory;
        int order = 0;
        for (String registryAddress : registries) {
            subscribeZookeeper(order++, beanRegistry, registryAddress);
        }
    }

    @Override
    public void setEnvironment(@Nonnull Environment environment) {
        this.env = (ConfigurableEnvironment) environment;
    }

    private void subscribeZookeeper(int order, BeanDefinitionRegistry beanRegistry, String registryAddress) {
        String regName = "reg_" + order;
        String file = String.format("%s/gateway_registry/%s.cache", System.getProperty("user.home"), regName);
        String beanName = String.format("<dubbo:registry id='%s' addr='%s'>", regName, registryAddress);
        LOGGER.info("Create dubbo registry " + beanName);
        beanRegistry.registerBeanDefinition(beanName, BeanDefinitionBuilder
                .rootBeanDefinition(RegistryConfig.class)
                .addPropertyValue("id", regName)
                .addPropertyValue("protocol", "zookeeper")
                .addPropertyValue("address", registryAddress)
                .addPropertyValue("file", file)
                .addPropertyValue("register", Boolean.FALSE)
                .getBeanDefinition());
    }


    /**
     * 获取注册中心地址
     *
     * @param env 环境变量
     * @return 注册中心地址
     */
    private List<String> parseRegistries(ConfigurableEnvironment env) {
        String registryValue = env.getProperty(ZOOKEEPER_REGISTRY);
        if (StringUtils.isEmpty(registryValue)) {
            throw new BeanCreationException("缺少配置 {" + ZOOKEEPER_REGISTRY + "}");
        }
        return Stream.of(registryValue.split(";"))
                .filter(reg -> !StringUtils.isEmpty(reg))
                .collect(Collectors.toList());
    }
}
