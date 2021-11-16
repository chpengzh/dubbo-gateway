package org.apache.dubbo.gateway.admin.service.event;

import org.apache.dubbo.gateway.api.model.EventMessage;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * 数据同步服务
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 11:03
 */
public interface EventService {

    /**
     * 事件发布接口
     *
     * @param eventKey     事件key
     * @param eventContent 时间消息体
     * @return 发布事件版本号
     */
    @Nonnull
    Long publish(String eventKey, List<String> eventContent);

    /**
     * 发布事件，不发送事件
     *
     * @param eventKey      事件key
     * @param eventContents 事件消息体
     * @return 发布事件版本号
     */
    Long appendEvents(String eventKey, List<String> eventContents);

    /**
     * 查询大于某个版本的事件
     *
     * @param eventKey 事件key
     * @param version  查询起始版本号(不包含)
     * @return 事件集合
     */
    @Nonnull
    List<EventMessage> queryMessages(@Nonnull String eventKey, @Nonnull Long version, boolean forceMaster);

    /**
     * 查询事件版本号
     *
     * @param eventKey 事件key
     * @return 查询版本号
     */
    @Nonnull
    Map<String, Long> queryLatestVersion(@Nonnull List<String> eventKey);
}
