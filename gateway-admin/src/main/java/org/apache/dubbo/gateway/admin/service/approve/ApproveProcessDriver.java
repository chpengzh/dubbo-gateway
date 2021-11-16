package org.apache.dubbo.gateway.admin.service.approve;

import org.apache.dubbo.gateway.admin.service.model.ApproveProcessBO;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface ApproveProcessDriver<Callback extends Serializable> {

    /**
     * 审批流提交
     *
     * @param process 审批流表单
     */
    void doSubmit(@Nonnull ApproveProcessService processService, @Nonnull ApproveProcessBO process);

    /**
     * 审批流回调
     *
     * @param processService 审批流服务
     * @param process        审批流回调
     */
    default void doCallback(@Nonnull ApproveProcessService processService, @Nonnull Callback process) {
    }

}
