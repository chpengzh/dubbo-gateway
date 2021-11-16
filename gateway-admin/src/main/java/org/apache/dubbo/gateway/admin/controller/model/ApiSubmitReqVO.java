package org.apache.dubbo.gateway.admin.controller.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建接口请求入参.
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApiSubmitReqVO implements Serializable {

    private static final int serialVersionUID = 0x11;

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
     * 鉴权类型, {@link AuthType}
     */
    private Integer authType;

    /**
     * 发布参数
     */
    private List<ApiParam> params;

    @Data
    @NoArgsConstructor
    public static class ApiParam implements Serializable {

        private static final long serialVersionUID = 0x11;

        /**
         * 客户端参数名称
         */
        private String sourceName;

        /**
         * @see ApiParamType
         */
        private Integer paramType;

        /**
         * 服务端接口参数名称
         */
        private String destName;

        /**
         * 接口参数默认值
         */
        private String defaultValue;

        /**
         * 接口参数描述
         */
        private String description;

        /**
         * 0 - 非必须，1 - 必须
         */
        private Byte required;

        /**
         * 映射服务端类名
         */
        private String className;

        /**
         * 手动关联子参数
         */
        private List<ApiParam> params;

    }
}
