package org.apache.dubbo.gateway.admin.controller.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 创建接口请求入参.
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiSyncReqVO extends ApiSubmitReqVO {

    /**
     * 同步环境携带用户名信息
     */
    private String username;

}
