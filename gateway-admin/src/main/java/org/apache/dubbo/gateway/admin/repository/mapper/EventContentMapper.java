package org.apache.dubbo.gateway.admin.repository.mapper;

import org.apache.dubbo.gateway.admin.repository.model.EventMessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 11:57
 */
@Mapper
public interface EventContentMapper {

    /**
     * 插入事件
     *
     * @param message 事件对象
     */
    void insert(@Nonnull EventMessageDO message);

    /**
     * 查询事件
     *
     * @param eventKey 事件Key
     * @param version  事件版本
     * @return 查询结果
     */
    @Nonnull
    List<EventMessageDO> queryAfter(
            @Param("eventKey") String eventKey,
            @Param("version") Long version,
            @Param("forceMaster") boolean forceMaster
    );

}
