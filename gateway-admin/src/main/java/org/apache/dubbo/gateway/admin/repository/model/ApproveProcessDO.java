package org.apache.dubbo.gateway.admin.repository.model;

import lombok.Data;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApproveProcessDO {

    private Long id;

    /**
     * 审批流id1
     */
    private String approveId;

    /**
     * 审批单url
     */
    private String url;

    /**
     * 审批流类型
     */
    private Integer type;

    /**
     * 关联对象id
     */
    private String linkId;

    /**
     * 提交审批人
     */
    private String username;

    /**
     * 审批提交上下文
     */
    private String payload;

    /**
     * 互斥状态锁
     */
    private String lockKey;

    /**
     * 审批结果
     */
    private Integer result;

}
