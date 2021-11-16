package org.apache.dubbo.gateway.admin.repository;

import org.apache.dubbo.gateway.admin.service.model.ApproveProcessBO;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessQueryBO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface ApproveProcessRepository {

    /**
     * 根据流程id获取审批单, 该操作会获取审批单行锁
     *
     * @param approveId 审批单
     * @return {@code null} 如果审批单不存在
     */
    @Nullable
    ApproveProcessBO getByApproveIdForLock(@Nonnull String approveId);

    /**
     * 根据互斥键获取审批单, 该操作会获取审批单行锁
     *
     * @param lockKey 加锁字段
     * @return {@code null} 如果审批单不存在
     */
    @Nullable
    ApproveProcessBO getLockedProcessForLock(@Nonnull String lockKey);

    /**
     * 创建审批单
     *
     * @param process 审批单
     */
    void create(@Nonnull ApproveProcessBO process);

    /**
     * 变更审批单
     *
     * @param process 审批单
     */
    void update(@Nonnull ApproveProcessBO process);

    /**
     * 查询审批单信息
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @Nonnull
    List<ApproveProcessBO> query(@Nonnull ApproveProcessQueryBO query);

}
