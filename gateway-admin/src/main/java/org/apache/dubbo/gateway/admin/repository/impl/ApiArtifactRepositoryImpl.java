package org.apache.dubbo.gateway.admin.repository.impl;

import org.apache.dubbo.gateway.admin.repository.ApiArtifactRepository;
import org.apache.dubbo.gateway.admin.repository.mapper.ApiArtifactMapper;
import org.apache.dubbo.gateway.admin.repository.model.ApiArtifactDO;
import org.apache.dubbo.gateway.admin.repository.model.ApiArtifactQueryDO;
import org.apache.dubbo.gateway.admin.service.model.ApiArtifactBO;
import org.apache.dubbo.gateway.admin.service.model.ApiArtifactQueryBO;
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
public class ApiArtifactRepositoryImpl implements ApiArtifactRepository {

    @Resource
    private ApiArtifactMapper mapper;

    @Nullable
    @Override
    public ApiArtifactBO getByReleaseVersion(@Nonnull String releaseVersion) {
        return Optional.ofNullable(mapper.getByReleaseVersion(releaseVersion))
                .map(this::convert)
                .orElse(null);
    }

    @Nonnull
    @Override
    public List<ApiArtifactBO> query(@Nonnull ApiArtifactQueryBO query) {
        return mapper.query(convert(query))
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public void create(@Nonnull ApiArtifactBO artifact) {
        mapper.create(convert(artifact));
    }

    @Override
    public void update(@Nonnull ApiArtifactBO artifact) {
        mapper.update(convert(artifact));
    }

    private ApiArtifactQueryDO convert(ApiArtifactQueryBO query) {
        ApiArtifactQueryDO result = new ApiArtifactQueryDO();
        BeanUtils.copyProperties(query, result);
        return result;
    }

    private ApiArtifactBO convert(ApiArtifactDO artifact) {
        ApiArtifactBO result = new ApiArtifactBO();
        BeanUtils.copyProperties(artifact, result);
        return result;
    }

    private ApiArtifactDO convert(ApiArtifactBO artifact) {
        ApiArtifactDO result = new ApiArtifactDO();
        BeanUtils.copyProperties(artifact, result);
        return result;
    }
}
