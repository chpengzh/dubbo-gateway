package org.apache.dubbo.gateway.admin.repository;

import org.apache.dubbo.gateway.admin.service.model.ApiArtifactBO;
import org.apache.dubbo.gateway.admin.service.model.ApiArtifactQueryBO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface ApiArtifactRepository {

    /**
     * 根据releaseVersion查询版本数据
     *
     * @param releaseVersion 发布版本
     * @return {@code null} 如果版本不存在
     */
    @Nullable
    ApiArtifactBO getByReleaseVersion(@Nonnull String releaseVersion);

    /**
     * 根据接口与审批流状态查询版本
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @Nonnull
    List<ApiArtifactBO> query(@Nonnull ApiArtifactQueryBO query);

    /**
     * 创建一个接口版本
     *
     * @param artifact 接口版本信息
     */
    void create(@Nonnull ApiArtifactBO artifact);

    /**
     * 创建一个接口版本
     *
     * @param artifact 接口版本信息
     */
    void update(@Nonnull ApiArtifactBO artifact);
}
