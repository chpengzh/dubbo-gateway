package org.apache.dubbo.gateway.admin.repository.impl;

import org.apache.dubbo.gateway.admin.repository.EventContentRepository;
import org.apache.dubbo.gateway.admin.repository.mapper.EventContentMapper;
import org.apache.dubbo.gateway.admin.repository.model.EventMessageDO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.List;

/**
 * 消息事件数据源
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 11:57
 */
@Repository
public class EventContentRepositoryImpl implements EventContentRepository {

    @Resource
    private EventContentMapper mapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addEvent(@Nonnull List<EventMessageDO> messages) {
        messages.forEach(mapper::insert);
    }

    @Nonnull
    @Override
    public List<EventMessageDO> queryAfter(@Nonnull String eventKey, @Nonnull Long version, boolean forceMaster) {
        return mapper.queryAfter(eventKey, version, forceMaster);
    }
}
