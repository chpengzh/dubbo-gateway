package org.apache.dubbo.gateway.admin.repository.mapper;

import org.apache.dubbo.gateway.admin.repository.model.ApproveProcessDO;
import org.apache.dubbo.gateway.admin.repository.model.ApproveProcessQueryDO;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author chpengzh@foxmail.com
 */
@Mapper
public interface ApproveProcessMapper {

    @Nullable
    ApproveProcessDO getByApproveId(@Nonnull String approveId);

    @Nullable
    ApproveProcessDO getLockedProcess(@Nonnull String lockKey);

    void create(@Nonnull ApproveProcessDO process);

    void update(@Nonnull ApproveProcessDO process);

    @Nonnull
    List<ApproveProcessDO> query(ApproveProcessQueryDO process);
}
