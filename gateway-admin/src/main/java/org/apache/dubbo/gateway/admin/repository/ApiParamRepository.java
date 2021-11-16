package org.apache.dubbo.gateway.admin.repository;


import org.apache.dubbo.gateway.api.model.ApiInfo;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Huang Haocheng
 */
public interface ApiParamRepository {

    List<ApiInfo.ApiParam> query(@Nonnull String apiName);

    void insert(@Nonnull ApiInfo.ApiParam apiParam);

    int deleteByApi(@Nonnull String apiName);
}
