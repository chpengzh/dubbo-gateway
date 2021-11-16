package org.apache.dubbo.gateway.server.gateway;

import com.google.common.collect.Lists;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chpengzh@foxmail.com
 * @date 2020/9/22 13:29
 */
@Configuration
public class GatewayConfiguration {

    /**
     * 业务网关路由
     */
    private static final String PATH_GATEWAY = "/gateway";

    @Bean("gatewayServletRegistrationBean")
    public ServletRegistrationBean<?> createGatewayRegistryBean(GatewayServlet gatewayServlet) {
        ServletRegistrationBean<GatewayServlet> registrationBean = new ServletRegistrationBean<>();
        registrationBean.setServlet(gatewayServlet);
        registrationBean.setUrlMappings(Lists.newArrayList(PATH_GATEWAY));
        registrationBean.setLoadOnStartup(1);
        registrationBean.setName(GatewayServlet.class.getName());
        return registrationBean;
    }

}
