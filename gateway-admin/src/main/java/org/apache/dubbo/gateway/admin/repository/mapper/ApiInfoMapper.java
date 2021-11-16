package org.apache.dubbo.gateway.admin.repository.mapper;

import org.apache.dubbo.gateway.admin.repository.model.ApiInfoDO;
import org.apache.dubbo.gateway.admin.repository.model.ApiQueryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Huang Haocheng
 */
@Mapper
public interface ApiInfoMapper {

    int createApi(ApiInfoDO entity);

    int updateApi(ApiInfoDO entity);

    @Nonnull
    List<ApiInfoDO> query(@Nonnull ApiQueryDO apiQueryDO);

    List<ApiInfoDO> keywordQuery(
            @Param("keyword") @Nonnull String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    int deleteApi(@Param("apiName") String apiName);
}
