package org.apache.dubbo.gateway.admin.service.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 接口服务查询条件
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApiQueryBO implements Serializable {

    private static final int serialVersionUID = 0x11;

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
    private Integer offset = 0;

    /**
     * 查询数量
     */
    private Integer limit = 1;

}