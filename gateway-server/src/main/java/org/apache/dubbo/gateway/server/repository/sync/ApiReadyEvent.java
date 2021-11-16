package org.apache.dubbo.gateway.server.repository.sync;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

/**
 * @author chpengzh@foxmail.com
 */
public class ApiReadyEvent extends ApplicationEvent {

    public ApiReadyEvent(ApplicationContext source) {
        super(source);
    }
}
