package org.apache.dubbo.gateway.admin.repository.impl;

import org.apache.dubbo.gateway.admin.repository.ApproveProcessRepository;
import org.apache.dubbo.gateway.admin.repository.mapper.ApproveProcessMapper;
import org.apache.dubbo.gateway.admin.repository.model.ApproveProcessDO;
import org.apache.dubbo.gateway.admin.repository.model.ApproveProcessQueryDO;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessBO;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessQueryBO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chpengzh@foxmail.com
 */
@Repository
public class ApproveProcessRepositoryImpl implements ApproveProcessRepository {

    @Resource
    private ApproveProcessMapper mapper;

    @Nullable
    @Override
    public ApproveProcessBO getByApproveIdForLock(@Nonnull String approveId) {
        return Optional.ofNullable(mapper.getByApproveId(approveId))
                .map(this::convert)
                .orElse(null);
    }

    @Nullable
    @Override
    public ApproveProcessBO getLockedProcessForLock(@Nonnull String lockKey) {
        return Optional.ofNullable(mapper.getLockedProcess(lockKey))
                .map(this::convert)
                .orElse(null);
    }

    @Override
    public void create(@Nonnull ApproveProcessBO process) {
        mapper.create(convert(process));
    }

    @Override
    public void update(@Nonnull ApproveProcessBO process) {
        mapper.update(convert(process));
    }

    @Nonnull
    @Override
    public List<ApproveProcessBO> query(@Nonnull ApproveProcessQueryBO query) {
        return mapper.query(convert(query))
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private ApproveProcessQueryDO convert(ApproveProcessQueryBO query) {
        ApproveProcessQueryDO result = new ApproveProcessQueryDO();
        BeanUtils.copyProperties(query, result);
        return result;
    }

    private ApproveProcessBO convert(ApproveProcessDO process) {
        ApproveProcessBO result = new ApproveProcessBO();
        BeanUtils.copyProperties(process, result);
        return result;
    }

    private ApproveProcessDO convert(ApproveProcessBO process) {
        ApproveProcessDO result = new ApproveProcessDO();
        BeanUtils.copyProperties(process, result);
        return result;
    }
}
