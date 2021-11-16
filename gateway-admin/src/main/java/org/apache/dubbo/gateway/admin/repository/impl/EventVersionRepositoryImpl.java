package org.apache.dubbo.gateway.admin.repository.impl;

import org.apache.dubbo.gateway.admin.repository.EventVersionRepository;
import org.apache.dubbo.gateway.admin.repository.mapper.EventVersionMapper;
import org.apache.dubbo.gateway.admin.repository.model.EventVersionDO;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 11:58
 */
@Repository
public class EventVersionRepositoryImpl implements EventVersionRepository {

    @Resource
    private EventVersionMapper mapper;

    @Nonnull
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long nextVersion(@Nonnull String eventKey) {
        List<EventVersionDO> versions = mapper.queryVersion(Lists.newArrayList(eventKey), true);
        long newVersion;
        if (versions.isEmpty()) {
            newVersion = 1L;
            mapper.insert(eventKey, newVersion);
        } else {
            newVersion = versions.get(0).getVersion() + 1L;
            mapper.setVersion(eventKey, newVersion);
        }
        return newVersion;
    }

    @Nonnull
    @Override
    public List<EventVersionDO> queryLatestVersion(@Nonnull List<String> eventKey) {
        return mapper.queryVersion(eventKey, false);
    }

}
