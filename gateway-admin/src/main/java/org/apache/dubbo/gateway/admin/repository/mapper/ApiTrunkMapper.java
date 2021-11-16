package org.apache.dubbo.gateway.admin.repository.mapper;

import org.apache.dubbo.gateway.admin.repository.model.ApiTrunkDO;
import org.apache.dubbo.gateway.admin.repository.model.ApiTrunkQueryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author chpengzh@foxmail.com
 */
@Mapper
public interface ApiTrunkMapper {

    @Nullable
    ApiTrunkDO getApiTrunk(
            @Param("apiName") String apiName,
            @Param("apiVersion") String apiVersion,
            @Param("lock") boolean lock
    );

    void create(@Nonnull ApiTrunkDO trunk);

    int update(@Nonnull ApiTrunkDO trunk);

    int delete(@Param("apiName") String apiName);

    @Nonnull
    List<ApiTrunkDO> keywordQuery(
            @Param("keyword") @Nonnull String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Nonnull
    List<ApiTrunkDO> query(@Nonnull ApiTrunkQueryDO query);
}
