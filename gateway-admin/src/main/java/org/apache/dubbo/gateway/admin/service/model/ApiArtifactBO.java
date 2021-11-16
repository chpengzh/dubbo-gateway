package org.apache.dubbo.gateway.admin.service.model;

import lombok.Data;
import org.apache.dubbo.gateway.api.model.ApiInfo;
import org.apache.dubbo.gateway.api.model.ApiInfo.ApiParam;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApiArtifactBO implements Serializable {

    private static final int serialVersionUID = 0x11;

    private Long id;

    /***
     * 发布版本
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
     * dubbo接口
     */
    private String dubboService;

    /**
     * dubbo方法
     */
    private String dubboMethod;

    /**
     * dubbo group
     */
    private String dubboGroup;

    /**
     * dubbo version
     */
    private String dubboVersion;

    /**
     * 发布参数(json序列化存储), {@link ApiParam}
     */
    private String params;

    /**
     * 返回值类型
     */
    private Integer returnType;

    /**
     * 是否需要登录
     */
    private Integer requireLogin;

    /**
     * 是否需要应用授权
     */
    private Integer requireAppAuth;

    /**
     * 鉴权类型
     */
    private Integer authType;

    /**
     * 审批流id
     */
    private String approveId;

    /**
     * 审批流发起审批时间
     */
    private Date approveStart;

    /**
     * 审批通过时间
     */
    private Date approveSuccess;

    /**
     * 发起审批用户(upm用户)
     */
    private String approveUser;

    /**
     * 审批状态结果
     */
    private Integer approveResult;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 变更时间
     */
    private Date gmtModify;

}
