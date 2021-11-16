package org.apache.dubbo.gateway.admin.service.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApiTrunkQueryBO implements Serializable {

    private static final int serialVersionUID = 0x11;

    /**
     * 发布版本号
     */
    private String releaseVersion;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口版本号
     */
    private String apiVersion;

    /**
     * Dubbo方法名称
     */
    private String dubboService;

    /**
     * Dubbo方法签名
     */
    private String dubboMethod;

    /**
     * 查询偏移
     */
    private int offset = 0;

    /**
     * 查询数量
     */
    private int limit = 1;

}
