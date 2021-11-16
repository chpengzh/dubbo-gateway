package org.apache.dubbo.gateway.admin.service.approve.impl;

import org.apache.dubbo.gateway.admin.constants.ApproveResult;
import org.apache.dubbo.gateway.admin.constants.ApproveType;
import org.apache.dubbo.gateway.admin.constants.ErrorCode;
import org.apache.dubbo.gateway.admin.repository.ApproveProcessRepository;
import org.apache.dubbo.gateway.admin.service.approve.ApproveCallback;
import org.apache.dubbo.gateway.admin.service.approve.ApproveProcessDriver;
import org.apache.dubbo.gateway.admin.service.approve.ApproveProcessService;
import org.apache.dubbo.gateway.admin.service.approve.properties.ApproveProperties;
import org.apache.dubbo.gateway.admin.service.model.ApproveActionBO;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessBO;
import org.apache.dubbo.gateway.admin.service.model.ApproveProcessQueryBO;
import org.apache.dubbo.gateway.admin.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * 审批流服务
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Service
public class ApproveProcessServiceImpl implements ApproveProcessService {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ApproveProperties properties;

    @Autowired
    private ApproveProcessRepository repository;

    @Autowired
    private List<ApproveCallback> callbacks;

    @Nullable
    @Override
    public ApproveProcessBO getByProcessId(@Nonnull String approveid) {
        return repository.getByApproveIdForLock(approveid);
    }

    @Nonnull
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ApproveProcessBO submit(ApproveProcessBO process) {
        // 1.互斥状态锁判断
        ApproveProcessBO lockedProcess = repository.getLockedProcessForLock(process.getLockKey());
        if (lockedProcess != null && Objects.equals(lockedProcess.getApproveId(), process.getApproveId())) {
            // 1.1 幂等提交, 直接返回
            return lockedProcess;
        } else if (lockedProcess != null) {
            // 1.2 非幂等提交, 流程互斥冲突
            throw ErrorCode.APPROVE_ILLEGAL_STATE.abort("存在未审批的审批单"
                    + ", id=" + lockedProcess.getApproveId()
                    + ", url=" + lockedProcess.getUrl());
        }

        // 2.创建流程
        repository.create(process);

        // 3.审批驱动
        ApproveType type = ApproveType.fromCode(process.getType());
        //noinspection rawtypes
        Class<? extends ApproveProcessDriver> driverType = properties.getDriver()
                .getOrDefault(type, ApproveProperties.DriverTypeEnum.NONE)
                .getDriver();
        if (driverType != null) {
            ApproveProcessDriver<?> driver = context.getBean(driverType);
            driver.doSubmit(this, process);
        }
        return Objects.requireNonNull(repository.getByApproveIdForLock(process.getApproveId()));
    }

    @Nullable
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ApproveProcessBO approve(ApproveActionBO action) {
        ApproveProcessBO process = repository.getByApproveIdForLock(action.getProcessId());
        if (process == null) {
            return null;
        }
        switch (ApproveResult.fromCode(process.getResult())) {
            case STARTED:
                return approveCallback(process, action, ApproveResult.APPROVED);
            case REJECTED:
                throw ErrorCode.APPROVE_ILLEGAL_STATE.abort("当前审批单已经驳回，请重新发起审批");
            case APPROVED:
            case UNKNOWN:
            default:
                break;
        }
        return process;
    }

    @Nullable
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ApproveProcessBO reject(ApproveActionBO action) {
        ApproveProcessBO process = repository.getByApproveIdForLock(action.getProcessId());
        if (process == null) {
            return null;
        }
        switch (ApproveResult.fromCode(process.getResult())) {
            case STARTED:
                return approveCallback(process, action, ApproveResult.REJECTED);
            case APPROVED:
                throw ErrorCode.APPROVE_ILLEGAL_STATE.abort("当前审批单已经审批通过，请重新发起审批");
            case REJECTED:
            case UNKNOWN:
            default:
                break;
        }
        return process;
    }

    @Nonnull
    @Override
    public List<ApproveProcessBO> query(ApproveProcessQueryBO query) {
        return repository.query(query);
    }

    @Override
    public void updateUrl(@Nonnull String approveId, @Nonnull String url) {
        ApproveProcessBO process = new ApproveProcessBO();
        process.setApproveId(approveId);
        process.setUrl(url);
        repository.update(process);
    }

    /**
     * 审批结果回调
     *
     * @param action 审批结果表单
     * @param result 审批结果
     * @return 审批流信息
     */
    private ApproveProcessBO approveCallback(
            @Nonnull ApproveProcessBO process,
            @Nonnull ApproveActionBO action,
            @Nonnull ApproveResult result
    ) {
        ApproveProcessBO processToSave = DataUtils.clone(process, ApproveProcessBO.class);
        processToSave.setApproveId(action.getProcessId());
        processToSave.setPayload(action.getPayload());
        processToSave.setUsername(action.getUsername());
        processToSave.setResult(result.getCode());
        repository.update(processToSave);

        for (ApproveCallback callback : callbacks) {
            if (Objects.equals(callback.approveType().getCode(), process.getType())) {
                switch (result) {
                    case APPROVED:
                        callback.onApproved(process);
                        break;
                    case REJECTED:
                        callback.onRejected(process);
                        break;
                    default:
                        break;
                }
            }
        }
        return processToSave;
    }
}
