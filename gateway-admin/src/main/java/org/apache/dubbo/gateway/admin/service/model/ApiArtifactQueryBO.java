package org.apache.dubbo.gateway.admin.service.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApiArtifactQueryBO implements Serializable {

    private static final int serialVersionUID = 0x11;

    /**
     * 版本号
     */
    private String releaseVersion;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * 接口版本
     */
    private String apiVersion;

    /**
     * Dubbo接口名
     */
    private String dubboService;

    /**
     * Dubbo方法名
     */
    private String dubboMethod;

    /**
     * 审批流id
     */
    private String approveId;

    /**
     * 审批结果
     */
    private Integer approveResult;

    /**
     * 查询偏移
     */
    private int offset = 0;

    /**
     * 查询页大小
     */
    private int limit = 1;

}
