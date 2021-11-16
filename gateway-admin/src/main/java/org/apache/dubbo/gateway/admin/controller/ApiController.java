package org.apache.dubbo.gateway.admin.controller;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.gateway.admin.constants.ApproveResult;
import org.apache.dubbo.gateway.admin.controller.model.ApiInfoRespVO;
import org.apache.dubbo.gateway.admin.controller.model.ApiSubmitReqVO;
import org.apache.dubbo.gateway.admin.controller.model.RpcResult;
import org.apache.dubbo.gateway.admin.service.api.ApiPublicationService;
import org.apache.dubbo.gateway.admin.service.approve.ApproveProcessService;
import org.apache.dubbo.gateway.admin.service.model.*;
import org.apache.dubbo.gateway.admin.utils.RpcResultUtil;
import org.apache.dubbo.gateway.api.constants.ApiParamType;
import org.apache.dubbo.gateway.api.model.ApiInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 服务接口相关接口
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@RestController
@RequestMapping("/management/api")
public class ApiController {

    @Autowired
    private ApiPublicationService apiPublicationService;

    @Autowired
    private ApproveProcessService approveProcessService;

    /**
     * 提交发布接口
     */
    @RequestMapping(method = RequestMethod.POST)
    public RpcResult<ApproveProcessBO> submitApi(@RequestBody ApiSubmitReqVO request) {
        // 0. 补齐releaseVersion
        if (ObjectUtils.isEmpty(request.getReleaseVersion())) {
            request.setReleaseVersion(UUID.randomUUID().toString());
        }

        // 1. 写入
        ApiArtifactBO artifact = artifactFromVO(request);
        artifact.setApproveUser("system");
        ApproveProcessBO approveProcess = apiPublicationService.submitVersion(artifact);

        // 2. 填写返回值
        return RpcResultUtil.createSuccessResult(approveProcess);
    }

    /**
     * 接口下线(仅管理员权限)
     */
    @RequestMapping(value = "offline", method = RequestMethod.POST)
    public RpcResult<Void> offline(@RequestParam(name = "apiName") String apiName) {
        apiPublicationService.offline(apiName);
        return RpcResultUtil.createSuccessResult(null);
    }

    /**
     * 根据条件查询接口元数据
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public RpcResult<List<ApiTrunkBO>> queryApiTrunk(
            @RequestParam(name = "apiName", defaultValue = "") String apiName,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        ApiTrunkQueryBO condition = new ApiTrunkQueryBO();
        if (!StringUtils.isEmpty(apiName)) {
            condition.setApiName(apiName);
        }
        condition.setOffset((page - 1) * pageSize);
        condition.setLimit(pageSize);
        return RpcResultUtil.createSuccessResult(apiPublicationService.queryTrunkByCondition(condition));
    }

    /**
     * 关键字匹配接口元数据定义
     */
    @RequestMapping(value = "/keyword", method = RequestMethod.GET)
    public RpcResult<List<ApiTrunkBO>> queryApiByKeyword(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        return RpcResultUtil.createSuccessResult(apiPublicationService.queryTrunkByKeyword(
                keyword,
                (page - 1) * pageSize,
                pageSize));
    }

    /**
     * 获取接口已发布版本详情
     */
    @RequestMapping(value = "/release", method = RequestMethod.GET)
    public RpcResult<ApiInfoRespVO> queryApiReleaseVersion(@RequestParam(name = "apiName") String apiName) {
        ApiUniqKey uniqKey = new ApiUniqKey();
        uniqKey.setApiName(apiName);
        ApiArtifactBO artifact = apiPublicationService.queryTrunkVersion(uniqKey);

        if (artifact == null) {
            return RpcResultUtil.createSuccessResult(null);
        }

        ApiInfoRespVO result = artifactToVO(artifact);
        return RpcResultUtil.createSuccessResult(result);
    }

    /**
     * 获取接口历史版本详情
     */
    @RequestMapping(value = "/versions", method = RequestMethod.GET)
    public RpcResult<List<ApiInfoRespVO>> queryApiVersions(
            @RequestParam(name = "apiName", defaultValue = "") String apiName,
            @RequestParam(name = "releaseVersion", defaultValue = "") String releaseVersion,
            @RequestParam(name = "approveResult", defaultValue = "-1") int approveResult,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        ApiArtifactQueryBO condition = new ApiArtifactQueryBO();
        if (!ObjectUtils.isEmpty(apiName)) {
            condition.setApiName(apiName);
        }
        if (!ObjectUtils.isEmpty(releaseVersion)) {
            condition.setReleaseVersion(releaseVersion);
        }
        if (approveResult >= 0) {
            condition.setApproveResult(approveResult);
        }
        condition.setOffset((page - 1) * pageSize);
        condition.setLimit(pageSize);
        List<ApiArtifactBO> artifacts = apiPublicationService.queryVersions(condition);

        List<ApiInfoRespVO> result = artifacts.stream()
                .map(this::artifactToVO)
                .collect(Collectors.toList());
        return RpcResultUtil.createSuccessResult(result);
    }

    /**
     * 获取当前待审批版本
     */
    @RequestMapping(value = "/version/approving", method = RequestMethod.GET)
    public RpcResult<ApiInfoRespVO> queryApproveVersion(
            @RequestParam(name = "apiName", defaultValue = "") String apiName
    ) {
        ApiArtifactQueryBO condition = new ApiArtifactQueryBO();
        condition.setApiName(apiName);
        condition.setLimit(1);
        List<ApiArtifactBO> artifacts = apiPublicationService.queryVersions(condition);
        if (artifacts.isEmpty()) {
            return RpcResultUtil.createSuccessResult(null);
        }

        ApproveProcessBO process = approveProcessService.getByProcessId(artifacts.get(0).getApproveId());
        if (process != null && Objects.equals(process.getResult(), ApproveResult.STARTED.getCode())) {
            ApiInfoRespVO response = artifactToVO(artifacts.get(0));
            return RpcResultUtil.createSuccessResult(response);
        }
        return RpcResultUtil.createSuccessResult(null);
    }

    /**
     * 接口表单详情 BO --> VO
     *
     * @param artifact BO入参
     * @return VO响应
     */
    @Nonnull
    private ApiInfoRespVO artifactToVO(@Nonnull ApiArtifactBO artifact) {
        List<ApiSubmitReqVO.ApiParam> params = Optional
                .ofNullable(artifact.getParams())
                .map(it -> JSON.parseArray(it, ApiInfo.ApiParam.class))
                .orElse(Collections.emptyList())
                .stream()
                .map(paramToVO())
                .collect(Collectors.toList());

        ApiInfoRespVO result = new ApiInfoRespVO();
        BeanUtils.copyProperties(artifact, result, "params");
        result.setParams(params);
        result.setArtifact(artifact);
        result.getClient().setBizParams(getClientBizParams(artifact));
        return result;
    }

    @Nonnull
    @SuppressWarnings("Duplicates")
    private Function<ApiInfo.ApiParam, ApiSubmitReqVO.ApiParam> paramToVO() {
        return new Function<ApiInfo.ApiParam, ApiSubmitReqVO.ApiParam>() {
            @Override
            public ApiSubmitReqVO.ApiParam apply(ApiInfo.ApiParam apiParam) {
                ApiSubmitReqVO.ApiParam result = new ApiSubmitReqVO.ApiParam();
                BeanUtils.copyProperties(apiParam, result);
                if (!CollectionUtils.isEmpty(apiParam.getParams())) {
                    result.setParams(apiParam.getParams()
                            .stream()
                            .map(this)
                            .collect(Collectors.toList()));
                }
                return result;
            }
        };
    }


    /**
     * 解析客户端应该携带的系统参数列表
     *
     * @param artifact 调用接口版本信息
     * @return 参数列表
     */
    private List<ApiSubmitReqVO.ApiParam> getClientBizParams(ApiArtifactBO artifact) {
        List<ApiSubmitReqVO.ApiParam> result = new ArrayList<>();
        new Consumer<Supplier<List<ApiInfo.ApiParam>>>() {
            @Override
            public void accept(Supplier<List<ApiInfo.ApiParam>> next) {
                Optional.ofNullable(next.get()).orElse(Collections.emptyList()).forEach(param -> {
                    if (ApiParamType.getByCode(param.getParamType()) == ApiParamType.OBJECT) {
                        accept(param::getParams);
                    } else {
                        result.add(paramToVO().apply(param));
                    }
                });
            }
        }.accept(() -> JSON.parseArray(artifact.getParams(), ApiInfo.ApiParam.class));
        return result.stream().filter(distinctBy(ApiSubmitReqVO.ApiParam::getSourceName)).collect(Collectors.toList());
    }

    /**
     * 接口表单详情 VO --> BO
     *
     * @param request VO入参
     * @return BO响应
     */
    @Nonnull
    private ApiArtifactBO artifactFromVO(@Nonnull ApiSubmitReqVO request) {
        List<ApiInfo.ApiParam> params = Optional
                .ofNullable(request.getParams())
                .orElse(Collections.emptyList())
                .stream()
                .map(paramFromVO(request.getApiName()))
                .collect(Collectors.toList());

        ApiArtifactBO artifact = new ApiArtifactBO();
        BeanUtils.copyProperties(request, artifact, "params");
        artifact.setParams(JSON.toJSONString(params));
        return artifact;
    }

    @Nonnull
    private Function<ApiSubmitReqVO.ApiParam, ApiInfo.ApiParam> paramFromVO(@Nonnull String apiName) {
        return new Function<ApiSubmitReqVO.ApiParam, ApiInfo.ApiParam>() {
            @Override
            public ApiInfo.ApiParam apply(@Nonnull ApiSubmitReqVO.ApiParam child) {
                ApiInfo.ApiParam next = new ApiInfo.ApiParam();
                BeanUtils.copyProperties(child, next);
                next.setApiName(apiName);
                if (!CollectionUtils.isEmpty(child.getParams())) {
                    next.setParams(child.getParams()
                            .stream()
                            .map(this)
                            .collect(Collectors.toList()));
                }
                return next;
            }
        };
    }

    private static <T, R> Predicate<T> distinctBy(Function<? super T, R> keyExtractor) {
        Set<R> exists = ConcurrentHashMap.newKeySet();
        return t -> exists.add(keyExtractor.apply(t));
    }
}
