package org.apache.dubbo.gateway.admin.repository.impl;

import org.apache.dubbo.gateway.admin.repository.ApiInfoRepository;
import org.apache.dubbo.gateway.admin.repository.mapper.ApiInfoMapper;
import org.apache.dubbo.gateway.admin.repository.model.ApiInfoDO;
import org.apache.dubbo.gateway.admin.repository.model.ApiQueryDO;
import org.apache.dubbo.gateway.admin.service.model.ApiQueryBO;
import org.apache.dubbo.gateway.api.model.ApiInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Huang Haocheng
 */
@Repository
public class ApiInfoRepositoryImpl implements ApiInfoRepository {

    @Resource
    private ApiInfoMapper apiInfoMapper;

    @Nonnull
    @Override
    public List<ApiInfo> keywordQuery(String keyword, int offset, int limit) {
        return apiInfoMapper.keywordQuery(keyword, offset, limit)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<ApiInfo> query(ApiQueryBO query) {
        return apiInfoMapper.query(convert(query))
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public void insert(@Nonnull ApiInfo apiInfo) {
        apiInfoMapper.createApi(convert(apiInfo));
    }

    @Override
    public int update(@Nonnull ApiInfo apiInfo) {
        return apiInfoMapper.updateApi(convert(apiInfo));
    }

    @Override
    public int delete(@Nonnull String apiName) {
        return apiInfoMapper.deleteApi(apiName);
    }

    private ApiQueryDO convert(ApiQueryBO query) {
        ApiQueryDO result = new ApiQueryDO();
        BeanUtils.copyProperties(query, result);
        return result;
    }

    private ApiInfo convert(ApiInfoDO api) {
        ApiInfo result = new ApiInfo();
        BeanUtils.copyProperties(api, result);
        return result;
    }

    private ApiInfoDO convert(ApiInfo api) {
        ApiInfoDO result = new ApiInfoDO();
        BeanUtils.copyProperties(api, result);
        return result;
    }
}
