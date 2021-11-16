package org.apache.dubbo.gateway.admin.controller.model;

import org.apache.dubbo.gateway.admin.service.model.ApiArtifactBO;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

/**
 * 创建接口请求入参.
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiInfoRespVO extends ApiSubmitReqVO {

    private static final int serialVersionUID = 0x11;

    @JsonUnwrapped(prefix = "raw.")
    private ApiArtifactBO artifact = new ApiArtifactBO();

    @JsonUnwrapped(prefix = "client.")
    private ClientParam client = new ClientParam();

    @Data
    public static class ClientParam {

        /**
         * 客户端传递的系统参数
         */
        private List<ApiParam> systemParams = Collections.emptyList();

        /**
         * 客户端传递的业务参数
         */
        private List<ApiParam> bizParams;

    }
}
