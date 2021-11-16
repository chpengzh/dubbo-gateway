package org.apache.dubbo.gateway.admin.repository.impl;

import org.apache.dubbo.gateway.admin.repository.ApiTrunkRepository;
import org.apache.dubbo.gateway.admin.repository.mapper.ApiTrunkMapper;
import org.apache.dubbo.gateway.admin.repository.model.ApiTrunkDO;
import org.apache.dubbo.gateway.admin.repository.model.ApiTrunkQueryDO;
import org.apache.dubbo.gateway.admin.service.model.ApiTrunkBO;
import org.apache.dubbo.gateway.admin.service.model.ApiTrunkQueryBO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Repository
public class ApiTrunkRepositoryImpl implements ApiTrunkRepository {

    @Resource
    private ApiTrunkMapper mapper;

    @Nullable
    @Override
    public ApiTrunkBO getApiTrunk(String apiName, String apiVersion, boolean lock) {
        return Optional.ofNullable(mapper.getApiTrunk(apiName, apiVersion, lock))
                .map(this::convert)
                .orElse(null);
    }

    @Override
    public void create(@Nonnull ApiTrunkBO trunk) {
        mapper.create(convert(trunk));
    }

    @Override
    public int update(@Nonnull ApiTrunkBO trunk) {
        return mapper.update(convert(trunk));
    }

    @Override
    public int delete(@Nonnull String apiName) {
        return mapper.delete(apiName);
    }

    @Nonnull
    @Override
    public List<ApiTrunkBO> keywordQuery(@Nonnull String keyword, int offset, int limit) {
        return mapper.keywordQuery(keyword, offset, limit)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<ApiTrunkBO> query(@Nonnull ApiTrunkQueryBO query) {
        return mapper.query(convert(query))
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private ApiTrunkQueryDO convert(ApiTrunkQueryBO query) {
        ApiTrunkQueryDO result = new ApiTrunkQueryDO();
        BeanUtils.copyProperties(query, result);
        return result;
    }

    private ApiTrunkBO convert(@Nonnull ApiTrunkDO trunk) {
        ApiTrunkBO result = new ApiTrunkBO();
        BeanUtils.copyProperties(trunk, result);
        return result;
    }

    private ApiTrunkDO convert(@Nonnull ApiTrunkBO trunk) {
        ApiTrunkDO result = new ApiTrunkDO();
        BeanUtils.copyProperties(trunk, result);
        return result;
    }
}
