package org.apache.dubbo.gateway.admin.service.approve.properties;

import lombok.Data;
import org.apache.dubbo.gateway.admin.constants.ApproveType;
import org.apache.dubbo.gateway.admin.service.approve.ApproveProcessDriver;
import org.apache.dubbo.gateway.admin.service.approve.driver.AutoApproveProcessDriver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
@Component
@ConfigurationProperties("gateway.approve")
public class ApproveProperties {

    private final Map<ApproveType, DriverTypeEnum> driver = new HashMap<>();

    @Data
    public static class ApolloPrinciple {

        /**
         * 命中审批规则表达式, 返回值为BPM审批单id
         */
        private String principle;

        /**
         * 当执行失败时，使用如下BPM配置来进行发布审批; 如果为空, 则默认自动审批.
         */
        private String defaultBpmDefinition;
    }

    public enum DriverTypeEnum {

        NONE(null),

        AUTO(AutoApproveProcessDriver.class);

        private final Class<? extends ApproveProcessDriver<?>> driver;

        DriverTypeEnum(Class<? extends ApproveProcessDriver<?>> driver) {
            this.driver = driver;
        }

        public Class<? extends ApproveProcessDriver<?>> getDriver() {
            return driver;
        }
    }
}
