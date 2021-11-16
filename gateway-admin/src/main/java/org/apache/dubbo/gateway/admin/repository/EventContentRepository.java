package org.apache.dubbo.gateway.admin.repository;

import org.apache.dubbo.gateway.admin.repository.model.EventMessageDO;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 事件数据源
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 11:45
 */
public interface EventContentRepository {

    /**
     * 添加事件
     *
     * @param messages 消息事件
     */
    void addEvent(@Nonnull List<EventMessageDO> messages);

    /**
     * 根据版本查询事件
     *
     * @param eventKey    事件key
     * @param version     查询版本
     * @param forceMaster 强一致查询
     */
    @Nonnull
    List<EventMessageDO> queryAfter(@Nonnull String eventKey, @Nonnull Long version, boolean forceMaster);

}
