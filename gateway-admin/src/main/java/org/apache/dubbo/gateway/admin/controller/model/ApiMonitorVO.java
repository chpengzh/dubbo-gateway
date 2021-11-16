package org.apache.dubbo.gateway.admin.controller.model;

import lombok.Data;

/**
 * @author chpengzh@foxmail.com
 */
@Data
public class ApiMonitorVO {

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 环境名称, {@link EnvNameEnum#getName()}
     */
    private String envName;

}
