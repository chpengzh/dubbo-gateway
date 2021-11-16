package org.apache.dubbo.gateway.admin.repository.model;

import lombok.Data;

import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApiQueryDO {

    /**
     * 查询租户信息
     */
    private List<String> tenantUuids;

    /**
     * 查询分组信息
     */
    private String groupUuid;

    /**
     * 查询服务名称 apiName
     */
    private String name;

    /**
     * 查询服务版本 apiVersion
     */
    private String version;

    /**
     * 查询Dubbo接口名称
     */
    private String service;

    /**
     * 查询Dubbo方法名称
     */
    private String method;

    /**
     * 是否为生产接口
     */
    private Boolean online;

    /**
     * 查询偏移
     */
    private Integer offset;

    /**
     * 查询数量
     */
    private Integer limit;

}
