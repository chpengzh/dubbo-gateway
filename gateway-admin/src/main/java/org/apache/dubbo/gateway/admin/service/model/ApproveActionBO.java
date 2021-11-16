package org.apache.dubbo.gateway.admin.service.model;

import lombok.Data;

/**
 * 审批结果
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApproveActionBO {

    /**
     * 审批流id
     */
    private String processId;

    /**
     * 审批人用户名
     */
    private String username;

    /**
     * 审批记录
     */
    private String payload;

}
