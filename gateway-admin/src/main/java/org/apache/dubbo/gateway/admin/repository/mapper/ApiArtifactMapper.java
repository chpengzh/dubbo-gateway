package org.apache.dubbo.gateway.admin.repository.mapper;

import org.apache.dubbo.gateway.admin.repository.model.ApiArtifactDO;
import org.apache.dubbo.gateway.admin.repository.model.ApiArtifactQueryDO;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author chpengzh@foxmail.com
 */
@Mapper
public interface ApiArtifactMapper {

    @Nullable
    ApiArtifactDO getByReleaseVersion(@Nonnull String releaseVersion);

    @Nonnull
    List<ApiArtifactDO> query(@Nonnull ApiArtifactQueryDO query);

    void create(@Nonnull ApiArtifactDO artifact);

    void update(@Nonnull ApiArtifactDO artifact);
}
