package org.apache.dubbo.gateway.admin.repository.impl;

import org.apache.dubbo.gateway.admin.repository.ApiParamRepository;
import org.apache.dubbo.gateway.admin.repository.mapper.ApiParamMapper;
import org.apache.dubbo.gateway.admin.repository.model.ApiParamDO;
import org.apache.dubbo.gateway.api.model.ApiInfo.ApiParam;
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
public class ApiParamRepositoryImpl implements ApiParamRepository {

    @Resource
    private ApiParamMapper apiParamMapper;

    @Override
    public List<ApiParam> query(@Nonnull String apiName) {
        return apiParamMapper.queryByApi(apiName)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public void insert(@Nonnull ApiParam apiParam) {
        apiParamMapper.createApiParam(convert(apiParam));
    }

    @Override
    public int deleteByApi(@Nonnull String apiName) {
        return apiParamMapper.deleteByApi(apiName);
    }

    private ApiParam convert(ApiParamDO param) {
        ApiParam result = new ApiParam();
        BeanUtils.copyProperties(param, result);
        return result;
    }

    private ApiParamDO convert(ApiParam param) {
        ApiParamDO result = new ApiParamDO();
        BeanUtils.copyProperties(param, result);
        return result;
    }
}
