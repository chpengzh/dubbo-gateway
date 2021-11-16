package org.apache.dubbo.gateway.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author chpengzh@foxmail.com
 */
@ImportResource({
        "classpath:gateway-chain.xml",
        "classpath:dubbo/dubbo-base.xml",
})
@SpringBootApplication
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

}