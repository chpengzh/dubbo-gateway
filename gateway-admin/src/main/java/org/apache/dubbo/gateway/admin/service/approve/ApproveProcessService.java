package org.apache.dubbo.gateway.admin.service.approve;

import org.apache.dubbo.gateway.admin.service.model.ApproveActionBO;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessBO;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessQueryBO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 审批流服务
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface ApproveProcessService {

    /**
     * 获取审批流表单
     *
     * @param approveid 审批流id
     * @return {@code null} 如果审批流不存在
     */
    @Nullable
    ApproveProcessBO getByProcessId(@Nonnull String approveid);

    /**
     * 提交审批单
     *
     * @param process 提交审批单
     */
    @Nonnull
    ApproveProcessBO submit(ApproveProcessBO process);

    /**
     * 回绝审批单
     *
     * @param action 审批通过信息
     */
    @Nullable
    ApproveProcessBO approve(ApproveActionBO action);

    /**
     * 回绝审批单
     *
     * @param action 回绝信息
     */
    @Nullable
    ApproveProcessBO reject(ApproveActionBO action);

    /**
     * 查询审批单
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @Nonnull
    List<ApproveProcessBO> query(ApproveProcessQueryBO query);

    /**
     * 变更审批单url
     *
     * @param approveId 审批流id
     * @param url       外部关联url
     */
    void updateUrl(@Nonnull String approveId, @Nonnull String url);

}
