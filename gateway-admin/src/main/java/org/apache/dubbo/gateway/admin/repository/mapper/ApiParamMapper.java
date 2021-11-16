package org.apache.dubbo.gateway.admin.repository.mapper;

import org.apache.dubbo.gateway.admin.repository.model.ApiParamDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Huang Haocheng
 */
@Mapper
public interface ApiParamMapper {

    @Nonnull
    List<ApiParamDO> queryByApi(@Param("apiName") String apiName);

    void createApiParam(@Nonnull ApiParamDO param);

    int deleteByApi(@Param("apiName") String apiName);
}
