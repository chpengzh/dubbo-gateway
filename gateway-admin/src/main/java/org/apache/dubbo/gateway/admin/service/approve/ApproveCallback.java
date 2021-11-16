package org.apache.dubbo.gateway.admin.service.approve;

import org.apache.dubbo.gateway.admin.constants.ApproveType;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessBO;

import javax.annotation.Nonnull;

/**
 * 审批流回调接口
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface ApproveCallback {

    /**
     * @return 审批流名称(唯一)
     */
    @Nonnull
    ApproveType approveType();

    /**
     * 审批通过
     *
     * @param process 审批流表单
     */
    void onApproved(@Nonnull ApproveProcessBO process);

    /**
     * 审批回绝
     *
     * @param process 审批流表单
     */
    void onRejected(@Nonnull ApproveProcessBO process);

}
