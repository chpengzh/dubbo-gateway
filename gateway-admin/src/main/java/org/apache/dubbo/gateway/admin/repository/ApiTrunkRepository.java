package org.apache.dubbo.gateway.admin.repository;

import org.apache.dubbo.gateway.admin.service.model.ApiTrunkBO;
import org.apache.dubbo.gateway.admin.service.model.ApiTrunkQueryBO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface ApiTrunkRepository {

    /**
     * 查询接口元数据
     *
     * @param apiName    接口名称
     * @param apiVersion 接口版本
     * @param lock       是否获取行锁
     * @return {@code null} 如果元数据不存在，获取行锁失败
     */
    @Nullable
    ApiTrunkBO getApiTrunk(String apiName, String apiVersion, boolean lock);

    /**
     * 新增接口元数据
     *
     * @param trunk 接口元数据
     */
    void create(@Nonnull ApiTrunkBO trunk);

    /**
     * 变更接口元数据
     *
     * @param trunk 变更表单
     */
    int update(@Nonnull ApiTrunkBO trunk);

    /**
     * 删除接口元数据
     *
     * @param apiName    接口名称
     */
    int delete(@Nonnull String apiName);

    /**
     * 关键字匹配查询
     *
     * @param keyword 查询关键字
     * @param offset  查询偏移
     * @param limit   查询数量
     * @return 查询结果
     */
    @Nonnull
    List<ApiTrunkBO> keywordQuery(
            @Nonnull String keyword,
            int offset,
            int limit
    );

    /**
     * 根据条件查询
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @Nonnull
    List<ApiTrunkBO> query(@Nonnull ApiTrunkQueryBO query);
}
