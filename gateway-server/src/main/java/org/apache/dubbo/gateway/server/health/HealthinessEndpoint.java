package org.apache.dubbo.gateway.server.health;

import com.google.common.collect.Lists;
import org.apache.dubbo.gateway.server.repository.sync.ApiReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 健康检查注入点
 *
 * @author chpengzh@foxmail.com
 * @date 2020/11/3 12:15
 */
@Configuration
public class HealthinessEndpoint {

    /**
     * 健康检查路由
     */
    private static final String PATH_HEALTH = "/health";

    @Configuration
    @ConditionalOnProperty(value = "gateway.health.enable", matchIfMissing = true)
    public static class HealthinessServletConfiguration {

        @Bean("healthinessServletRegistrationBean")
        public ServletRegistrationBean<?> createHealthinessRegistryBean(HealthinessServlet healthinessServlet) {
            ServletRegistrationBean<HealthinessServlet> registrationBean = new ServletRegistrationBean<>();
            registrationBean.setServlet(healthinessServlet);
            registrationBean.setUrlMappings(Lists.newArrayList(PATH_HEALTH));
            registrationBean.setLoadOnStartup(2);
            registrationBean.setName(HealthinessServlet.class.getName());
            return registrationBean;
        }
    }

    @Component
    public static class HealthinessServlet extends HttpServlet implements ApplicationListener<ApiReadyEvent> {

        private static final Logger LOGGER = LoggerFactory.getLogger("healthinessLogger");

        private static final long INIT_TIMESTAMP = 0L;

        @Autowired
        private HealthinessProperties healthinessProperties;

        private final AtomicLong initTimestamp = new AtomicLong(INIT_TIMESTAMP);

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            long init = initTimestamp.get();
            boolean isReady = isReady(init);
            String raw = isReady
                    ? String.format("Ready at %s, now is %s.", toDate(init), toDate(System.currentTimeMillis()))
                    : "Not ready yet.";
            resp.getWriter().println(raw);
            resp.setStatus(isReady
                    ? HttpStatus.OK.value()
                    : HttpStatus.SERVICE_UNAVAILABLE.value());
            if (isReady) {
                LOGGER.info("healthy - " + raw);
            } else {
                LOGGER.warn("unhealthy - " + raw);
            }
        }

        private String toDate(long timestamp) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            return format.format(timestamp);
        }

        private boolean isReady(long init) {
            long now = System.currentTimeMillis();
            return init > INIT_TIMESTAMP
                    && TimeUnit.MILLISECONDS.toSeconds(now - init) > healthinessProperties.getInitLatencySec();
        }

        @Override
        public void onApplicationEvent(@Nonnull ApiReadyEvent event) {
            initTimestamp.set(System.currentTimeMillis());
        }
    }
}
