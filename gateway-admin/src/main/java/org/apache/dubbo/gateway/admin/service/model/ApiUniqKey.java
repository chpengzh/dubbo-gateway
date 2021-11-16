package org.apache.dubbo.gateway.admin.service.model;

import lombok.Data;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApiUniqKey {

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口版本
     */
    private String apiVersion;

}
