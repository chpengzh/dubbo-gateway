package org.apache.dubbo.gateway.admin.repository;

import org.apache.dubbo.gateway.admin.service.model.ApiQueryBO;
import org.apache.dubbo.gateway.api.model.ApiInfo;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Huang Haocheng
 */
public interface ApiInfoRepository {

    @Nonnull
    List<ApiInfo> keywordQuery(String keyword, int offset, int limit);

    @Nonnull
    List<ApiInfo> query(ApiQueryBO query);

    void insert(@Nonnull ApiInfo apiInfo);

    int update(@Nonnull ApiInfo apiInfo);

    int delete(@Nonnull String apiName);
}
