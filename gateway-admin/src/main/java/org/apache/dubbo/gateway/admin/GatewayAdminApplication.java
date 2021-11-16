package org.apache.dubbo.gateway.admin;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 10:51
 */
@SpringBootApplication
@ImportResource({
        "classpath:dubbo/dubbo-base.xml",
        "classpath:dubbo/dubbo-provider.xml",
        "classpath:dubbo/dubbo-event.xml",
})
@EnableDubbo
@MapperScan(annotationClass = Mapper.class, basePackages = "org.apache.dubbo.gateway.admin")
@EnableTransactionManagement
public class GatewayAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayAdminApplication.class, args);
    }

}
