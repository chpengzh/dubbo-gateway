package org.apache.dubbo.gateway.admin.repository.mapper;

import org.apache.dubbo.gateway.admin.repository.model.EventVersionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 事件版本管理表
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 11:57
 */
@Mapper
public interface EventVersionMapper {

    /**
     * 查询版本号并获取行锁
     *
     * @param eventKeys 事件Key
     * @return 最大版本号
     */
    @Nonnull
    List<EventVersionDO> queryVersion(@Param("eventKeys") @Nonnull List<String> eventKeys, @Param("lock") boolean lock);

    /**
     * 设置版本
     *
     * @param eventKey 事件Key
     * @param version  事件版本
     */
    void setVersion(@Nonnull @Param("eventKey") String eventKey, @Param("version") @Nonnull Long version);

    /**
     * 插入新版本
     *
     * @param eventKey 事件Key
     * @param version  事件版本
     */
    void insert(@Nonnull @Param("eventKey") String eventKey, @Param("version") @Nonnull Long version);
}
