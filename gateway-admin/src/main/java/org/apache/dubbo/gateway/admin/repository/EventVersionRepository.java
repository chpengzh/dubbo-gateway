package org.apache.dubbo.gateway.admin.repository;

import org.apache.dubbo.gateway.admin.repository.model.EventVersionDO;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 事件版本数据源
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 11:46
 */
public interface EventVersionRepository {

    /**
     * 升级版本并获取锁
     *
     * @param eventKey 事件key
     * @return 升级后版本
     */
    @Nonnull
    Long nextVersion(@Nonnull String eventKey);

    /**
     * 查询版本号
     *
     * @param eventKey 事件key
     * @return 事件版本
     */
    @Nonnull
    List<EventVersionDO> queryLatestVersion(@Nonnull List<String> eventKey);

}
