package org.apache.dubbo.gateway.admin.service.api.impl;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.gateway.admin.constants.ApproveResult;
import org.apache.dubbo.gateway.admin.constants.ApproveType;
import org.apache.dubbo.gateway.admin.constants.ErrorCode;
import org.apache.dubbo.gateway.admin.repository.ApiArtifactRepository;
import org.apache.dubbo.gateway.admin.repository.ApiTrunkRepository;
import org.apache.dubbo.gateway.admin.service.api.ApiPublicationService;
import org.apache.dubbo.gateway.admin.service.api.ApiReleaseService;
import org.apache.dubbo.gateway.admin.service.approve.ApproveCallback;
import org.apache.dubbo.gateway.admin.service.approve.ApproveProcessService;
import org.apache.dubbo.gateway.admin.service.model.*;
import org.apache.dubbo.gateway.admin.utils.DataUtils;
import org.apache.dubbo.gateway.api.model.ApiInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Service
public class ApiPublicationServiceImpl implements ApiPublicationService, ApproveCallback {

    @Autowired
    private ApiArtifactRepository artifactRepository;

    @Autowired
    private ApiTrunkRepository trunkRepository;

    @Autowired
    private ApproveProcessService approveProcessService;

    @Autowired
    private ApiReleaseService apiReleaseService;

    @Nonnull
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ApproveProcessBO submitVersion(@Nonnull ApiArtifactBO artifact) {
        // 1.幂等操作判断
        ApiArtifactBO current = artifactRepository.getByReleaseVersion(artifact.getReleaseVersion());
        if (current != null
                && Objects.equals(current.getApiName(), artifact.getApiName())
                && Objects.equals(current.getApiVersion(), artifact.getApiVersion())
                && !StringUtils.isEmpty(current.getApproveId())) {
            String approveProcessId = current.getApproveId();
            ApproveProcessBO processInAction = approveProcessService.getByProcessId(approveProcessId);
            assert processInAction != null;
            return processInAction;
        } else if (current != null) {
            throw ErrorCode.ILLEGAL_ARGS.abort("当前releaseVersion版本已存在，请勿重复提交.");
        }

        // 2.获取接口元数据行锁(或者通过创建获取行锁)
        lockApiTrunkAndGet(artifact);

        // 3.创建发布审批单, 审批流之间通过 lockKey + (result = 0) 来确保审批流互斥
        ApproveProcessBO approveForm = new ApproveProcessBO();
        approveForm.setApproveId(UUID.randomUUID().toString());
        approveForm.setType(ApproveType.API_PUBLICATION.getCode());
        approveForm.setLinkId(artifact.getReleaseVersion());
        approveForm.setUsername(artifact.getApproveUser());
        approveForm.setLockKey(String.format("%s-%s", artifact.getApiName(), artifact.getApiVersion()));

        // 4.写入版本数据
        ApiArtifactBO artifactToSafe = DataUtils.clone(artifact, ApiArtifactBO.class);
        artifactToSafe.setApproveId(approveForm.getApproveId());
        artifactToSafe.setApproveStart(new Date());
        artifactToSafe.setApproveUser(approveForm.getUsername());
        artifactToSafe.setApproveResult(ApproveResult.STARTED.getCode());
        artifactRepository.create(artifactToSafe);

        // *.提交发布审批单
        return approveProcessService.submit(approveForm);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void releaseVersion(@Nonnull ApiArtifactBO apiArtifact) {
        // 1.获取发布行锁
        ApiTrunkBO trunk = lockApiTrunkAndGet(apiArtifact);

        // 2.如果发布版本不一致，则执行发布
        if (!Objects.equals(trunk.getReleaseVersion(), apiArtifact.getReleaseVersion())) {
            ApiTrunkBO updateForm = new ApiTrunkBO();
            updateForm.setApiName(apiArtifact.getApiName());
            updateForm.setApiVersion(apiArtifact.getApiVersion());
            updateForm.setReleaseVersion(apiArtifact.getReleaseVersion());
            updateForm.setDubboService(apiArtifact.getDubboService());
            updateForm.setDubboMethod(apiArtifact.getDubboMethod());
            updateForm.setModifyUser(apiArtifact.getApproveUser());
            updateForm.setModifyUser(apiArtifact.getApproveUser());
            trunkRepository.update(updateForm);
        }

        // 3.填写DataCenter发布表单，调用接口执行发布
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setApiName(apiArtifact.getApiName());
        apiInfo.setServiceName(apiArtifact.getDubboService());
        apiInfo.setServiceVersion(apiArtifact.getDubboVersion());
        apiInfo.setServiceGroup(apiArtifact.getDubboGroup());
        apiInfo.setMethod(apiArtifact.getDubboMethod());
        apiInfo.setParams(JSON.parseArray(apiArtifact.getParams(), ApiInfo.ApiParam.class));
        apiReleaseService.publish(apiInfo);

        // 4.变更版本信息
        ApiArtifactBO updateForm = new ApiArtifactBO();
        updateForm.setReleaseVersion(apiArtifact.getReleaseVersion());
        updateForm.setApproveResult(ApproveResult.APPROVED.getCode());
        updateForm.setApproveSuccess(new Date());
        artifactRepository.update(updateForm);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void rejectVersion(@Nonnull ApiArtifactBO apiArtifact) {
        ApiArtifactBO updateForm = new ApiArtifactBO();
        updateForm.setReleaseVersion(apiArtifact.getReleaseVersion());
        updateForm.setApproveResult(ApproveResult.REJECTED.getCode());
        updateForm.setApproveSuccess(new Date());
        artifactRepository.update(updateForm);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void offline(@Nonnull String apiName) {
        trunkRepository.delete(apiName);
        apiReleaseService.offline(apiName);
    }

    @Nonnull
    @Override
    public ApproveType approveType() {
        return ApproveType.API_PUBLICATION;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void onApproved(@Nonnull ApproveProcessBO process) {
        String releaseVersion = process.getLinkId();
        ApiArtifactBO artifact = artifactRepository.getByReleaseVersion(releaseVersion);
        // 1.判断待发布版本是否存在
        if (artifact == null) {
            throw ErrorCode.APPROVE_ILLEGAL_STATE.abort("未找到待发布版本信息");
        }

        // 2.因为一个接口版本发布允许多个审批单，因此要比对审批单id是否匹配
        if (!Objects.equals(process.getApproveId(), artifact.getApproveId())) {
            throw ErrorCode.APPROVE_ILLEGAL_STATE.abort("当前接口存在新的审批单流程"
                    + ",当前审批通过id=" + process.getApproveId()
                    + ",已存在审批单id=" + artifact.getApproveId());
        }

        // *.审批状态判断成功，驱动接口发布
        releaseVersion(artifact);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void onRejected(@Nonnull ApproveProcessBO process) {
        String releaseVersion = process.getLinkId();
        ApiArtifactBO artifact = artifactRepository.getByReleaseVersion(releaseVersion);

        // 判断审批流是否有效
        if (artifact == null || !Objects.equals(process.getApproveId(), artifact.getApproveId())) {
            return;
        }

        // *.审批回绝，标记版本
        rejectVersion(artifact);
    }

    @Nonnull
    @Override
    public List<ApiTrunkBO> queryTrunkByKeyword(@Nonnull String keyword, int offset, int limit) {
        return trunkRepository.keywordQuery(keyword, offset, limit);
    }

    @Nonnull
    @Override
    public List<ApiTrunkBO> queryTrunkByCondition(@Nonnull ApiTrunkQueryBO query) {
        return trunkRepository.query(query);
    }

    @Nullable
    @Override
    public ApiArtifactBO queryTrunkVersion(@Nonnull ApiUniqKey apiUniqKey) {
        ApiTrunkBO trunk = trunkRepository.getApiTrunk(apiUniqKey.getApiName(), apiUniqKey.getApiVersion(), false);
        if (trunk == null || StringUtils.isEmpty(trunk.getReleaseVersion())) {
            return null;
        }
        return artifactRepository.getByReleaseVersion(trunk.getReleaseVersion());
    }

    @Nonnull
    @Override
    public List<ApiArtifactBO> queryVersions(@Nonnull ApiArtifactQueryBO query) {
        return artifactRepository.query(query);
    }

    /**
     * 获取API元数据行锁, 并返回加锁元数据信息
     *
     * @param artifact api版本
     * @return API元数据信息
     */
    @Nonnull
    private ApiTrunkBO lockApiTrunkAndGet(ApiArtifactBO artifact) {
        ApiTrunkBO trunk = trunkRepository.getApiTrunk(artifact.getApiName(), artifact.getApiVersion(), true);
        if (trunk != null) {
            return trunk;
        }
        ApiTrunkBO newTrunk = new ApiTrunkBO();
        newTrunk.setApiName(artifact.getApiName());
        newTrunk.setApiVersion(artifact.getApiVersion());
        newTrunk.setReleaseVersion(""); //未发布不写这个版本
        newTrunk.setDubboService(artifact.getDubboService());
        newTrunk.setDubboMethod(artifact.getDubboMethod());
        newTrunk.setOwner(artifact.getApproveUser());
        newTrunk.setModifyUser(artifact.getApproveUser());
        trunkRepository.create(newTrunk);
        return newTrunk;
    }
}
