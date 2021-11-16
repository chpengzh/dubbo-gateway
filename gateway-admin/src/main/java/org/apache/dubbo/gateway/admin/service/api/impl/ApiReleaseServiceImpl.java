package org.apache.dubbo.gateway.admin.service.api.impl;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.gateway.admin.repository.ApiInfoRepository;
import org.apache.dubbo.gateway.admin.repository.ApiParamRepository;
import org.apache.dubbo.gateway.admin.service.api.ApiReleaseService;
import org.apache.dubbo.gateway.admin.service.model.ApiQueryBO;
import org.apache.dubbo.gateway.admin.utils.DataUtils;
import org.apache.dubbo.gateway.api.constants.EventName;
import org.apache.dubbo.gateway.api.model.ApiInfo;
import org.apache.dubbo.gateway.api.model.ApiInfo.ApiParam;
import org.apache.dubbo.gateway.api.service.EventMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Service
public class ApiReleaseServiceImpl implements ApiReleaseService {

    @Resource
    private ApiInfoRepository apiInfoRepository;

    @Resource
    private ApiParamRepository apiParamRepository;

    @Resource
    private EventMessageService eventMessageService;

    @Nonnull
    @Override
    public List<ApiInfo> keywordQuery(@Nonnull String keyword, int offset, int limit) {
        List<ApiInfo> queryResult = apiInfoRepository.keywordQuery(keyword, offset, limit);
        queryResult.forEach(api -> api.setParams(findParams(api)));
        return queryResult;
    }

    @Nonnull
    @Override
    public List<ApiInfo> query(@Nonnull ApiQueryBO query) {
        List<ApiInfo> queryResult = apiInfoRepository.query(query);
        queryResult.forEach(api -> api.setParams(findParams(api)));
        return queryResult;
    }

    @Nullable
    @Override
    public ApiInfo findApi(@Nonnull String apiName, @Nonnull String apiVersion) {
        ApiQueryBO query = new ApiQueryBO();
        query.setName(apiName);
        query.setVersion(apiVersion);
        List<ApiInfo> queryResult = query(query);
        return queryResult.isEmpty()
                ? null
                : queryResult.get(0);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void publish(@Nonnull ApiInfo apiInfo) {
        // 1. 变更主数据状态
        createOrUpdateApiAsOnline(apiInfo);

        // 2. 删除并重建接口参数信息
        recreateAllParams(apiInfo);

        // 3. 发送同步消息, 响应成功
        eventMessageService.produce(EventName.API_UPGRADE, JSON.toJSONString(apiInfo));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void offline(@Nonnull String apiName) {
        apiParamRepository.deleteByApi(apiName);
        apiInfoRepository.delete(apiName);

        // 发送下线通知，响应成功
        ApiInfo deleted = new ApiInfo();
        deleted.setApiName(apiName);
        eventMessageService.produce(EventName.API_OFFLINE, JSON.toJSONString(deleted));
    }

    /**
     * 填充API参数信息
     */
    private List<ApiParam> findParams(ApiInfo apiInfo) {
        List<ApiParam> apiParams = apiParamRepository.query(apiInfo.getApiName()).stream().map(item -> {
            ApiParam apiParam = new ApiParam();
            BeanUtils.copyProperties(item, apiParam);
            return apiParam;
        }).collect(Collectors.toList());
        for (ApiParam parent : apiParams) {
            for (ApiParam child : apiParams) {
                if (Objects.equals(parent.getUuid(), child.getParentUuid())) {
                    List<ApiParam> subParams = Optional.ofNullable(parent.getParams()).orElse(new ArrayList<>());
                    subParams.add(child);
                    parent.setParams(subParams);
                }
            }
        }
        return apiParams.stream()
                .filter(it -> ObjectUtils.isEmpty(it.getParentUuid()))
                .collect(Collectors.toList());
    }

    /**
     * 创建或变更接口信息至上线状态
     *
     * @param apiInfo 接口信息
     */
    private void createOrUpdateApiAsOnline(@Nonnull ApiInfo apiInfo) {
        ApiInfo apiToSave = DataUtils.clone(apiInfo, ApiInfo.class);
        if (apiInfoRepository.update(apiToSave) == 0) {
            apiInfoRepository.insert(apiToSave);
        }
    }

    /**
     * 构造接口参数
     *
     * @param apiInfo 接口参数信息
     */
    private void recreateAllParams(ApiInfo apiInfo) {
        // 1. 删除旧接口关联参数.
        apiParamRepository.deleteByApi(apiInfo.getApiName());
        // 2. 重新写入新接口关联参数.
        for (ApiParam apiParam : new Function<List<ApiParam>, List<ApiParam>>() {
            @Override
            public List<ApiParam> apply(List<ApiParam> apiParams) {
                List<ApiParam> result = new ArrayList<>();
                if (apiParams == null || apiParams.isEmpty()) {
                    return result;
                }
                for (ApiParam next : apiParams) {
                    next.setUuid(UUID.randomUUID().toString());
                    result.add(next);
                    List<ApiParam> children = apply(next.getParams());
                    children.forEach(child -> child.setParentUuid(next.getUuid()));
                    result.addAll(children);
                }
                return result;
            }
        }.apply(apiInfo.getParams())) {
            apiParamRepository.insert(apiParam);
        }
    }
}
