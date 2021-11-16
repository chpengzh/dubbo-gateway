package org.apache.dubbo.gateway.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.dubbo.gateway.api.constants.ApiParamType;

import java.io.Serializable;
import java.util.List;

/**
 * @author chpengzh@foxmail.com
 */
@Data
@NoArgsConstructor
public class ApiInfo implements Serializable {

    private static final long serialVersionUID = 0x11;

    /**
     * 接口名称
     */
    private String apiName;

    /**
     * Dubbo接口类名(全限定名)
     */
    private String serviceName;

    /**
     * Dubbo接口提供者版本
     */
    private String serviceVersion;

    /**
     * Dubbo接口提供者group
     */
    private String serviceGroup;

    /**
     * Dubbo接口方法
     */
    private String method;

    /**
     * 请求参数
     */
    private List<ApiParam> params;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiParam implements Serializable {

        private static final long serialVersionUID = 0x11;

        /**
         * 接口名称
         */
        private String apiName;

        /**
         * 当前参数UUID
         */
        private String uuid;

        /**
         * 父级参数UUID, {@code null} 如果没有父级参数
         */
        private String parentUuid;

        /**
         * 客户端参数名称
         */
        private String sourceName;

        /**
         * 服务端参数名称
         */
        private String destName;

        /**
         * 参数类型
         *
         * @see ApiParamType
         */
        private Integer paramType;

        /**
         * 缺省值
         */
        private String defaultValue;

        /**
         * 0 - 非必须，1 - 必须
         */
        private Integer required;

        /**
         * 解析类名
         */
        private String className;

        /**
         * 手动关联子参数
         */
        private List<ApiParam> params;
    }
}
