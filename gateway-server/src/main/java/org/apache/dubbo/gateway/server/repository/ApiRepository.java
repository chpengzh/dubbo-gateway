package org.apache.dubbo.gateway.server.repository;

import org.apache.dubbo.gateway.api.model.ApiInfo;

import javax.annotation.Nullable;
import java.util.Collection;


/**
 * API数据源
 *
 * @author chpengzh@foxmail.com
 * @date 2020-09-24 10:10
 */
public interface ApiRepository {

    /**
     * 根据名称和版本号获取API
     *
     * @param apiName {@link ApiInfo#getApiName()}
     * @return {@code null} 如果不存在
     */
    @Nullable
    ApiInfo get(@Nullable String apiName);

    /**
     * 获取全量API信息
     *
     * @return 全量API信息
     */
    Collection<ApiInfo> getAll();

    /**
     * 创建或变更API
     *
     * @param api 待创建API
     */
    void createOrUpdate(ApiInfo api);

    /**
     * 删除API
     *
     * @param apiName {@link ApiInfo#getApiName()}
     */
    void delete(String apiName);

}
