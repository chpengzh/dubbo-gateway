package org.apache.dubbo.gateway.admin.repository.model;

import lombok.Data;

import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApproveProcessQueryDO {

    /**
     * 查询审批单号
     */
    private List<String> approveIds;

    /**
     * 审批类型, {@link org.apache.dubbo.gateway.admin.constants.ApproveType}
     */
    private Integer type;

    /**
     * 审批结果, {@link org.apache.dubbo.gateway.admin.constants.ApproveResult}
     */
    private Integer result;

    /**
     * 查询便宜
     */
    private int offset = 0;

    /**
     * 查询数量
     */
    private int limit = 1;
}
