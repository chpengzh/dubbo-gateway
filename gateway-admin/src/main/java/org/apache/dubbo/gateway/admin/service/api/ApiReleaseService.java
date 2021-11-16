package org.apache.dubbo.gateway.admin.service.api;

import org.apache.dubbo.gateway.admin.service.model.ApiQueryBO;
import org.apache.dubbo.gateway.api.model.ApiInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 生产数据管理服务
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface ApiReleaseService {

    /**
     * 模糊匹配
     *
     * @param keyword 匹配关键字
     * @param offset  查询偏移
     * @param limit   查询数量
     * @return 查询接口
     */
    @Nonnull
    List<ApiInfo> keywordQuery(@Nonnull String keyword, int offset, int limit);

    /**
     * 查询所有接口定义
     *
     * @param query 查询条件
     * @return 所有满足条件的接口定义
     */
    @Nonnull
    List<ApiInfo> query(@Nonnull ApiQueryBO query);

    /**
     * 获取某个接口定义
     *
     * @param apiName    接口名称
     * @param apiVersion 接口版本
     * @return 接口定义, {@code null} 如果接口不存在
     */
    @Nullable
    ApiInfo findApi(@Nonnull String apiName, @Nonnull String apiVersion);

    /**
     * 直接发布某个接口定义
     *
     * @param apiInfo 接口定义
     */
    void publish(@Nonnull ApiInfo apiInfo);

    /**
     * 下线某接口
     *
     * @param apiName    接口名称
     */
    void offline(@Nonnull String apiName);

}
