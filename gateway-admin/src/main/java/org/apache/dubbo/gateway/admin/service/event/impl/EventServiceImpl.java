package org.apache.dubbo.gateway.admin.service.event.impl;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.gateway.admin.constants.ErrorCode;
import org.apache.dubbo.gateway.admin.repository.EventContentRepository;
import org.apache.dubbo.gateway.admin.repository.EventVersionRepository;
import org.apache.dubbo.gateway.admin.repository.model.EventMessageDO;
import org.apache.dubbo.gateway.admin.repository.model.EventVersionDO;
import org.apache.dubbo.gateway.admin.service.event.EventBus;
import org.apache.dubbo.gateway.admin.service.event.EventService;
import org.apache.dubbo.gateway.api.model.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 事件同步服务实现
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 11:39
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventVersionRepository eventVersionRepository;

    @Autowired
    private EventContentRepository eventContentRepository;

    @Autowired
    private EventBus eventBus;

    @Autowired
    @Lazy
    private EventService thiz;

    @Nonnull
    @Override
    public Long publish(String eventKey, List<String> eventContents) {
        if (CollectionUtils.isEmpty(eventContents)) {
            throw ErrorCode.PUBLISH_EMPTY_MESSAGE.abort("eventKey=" + eventKey);
        }
        for (String msg : eventContents) {
            if (StringUtils.isEmpty(msg)) {
                throw ErrorCode.PUBLISH_EMPTY_MESSAGE.abort(
                        "eventKey=" + eventKey + ", content=" + JSON.toJSONString(eventContents));
            }
        }

        Long version = thiz.appendEvents(eventKey, eventContents);

        // 发送事件总线
        eventBus.publish(eventKey, eventContents.stream().map(content -> {
            EventMessage message = new EventMessage();
            message.setEventKey(eventKey);
            message.setContent(content);
            message.setVersion(version);
            return message;
        }).collect(Collectors.toList()));
        return version;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long appendEvents(String eventKey, List<String> eventContents) {
        // 写入版本号
        Long version = eventVersionRepository.nextVersion(eventKey);

        // 写入消息体
        eventContentRepository.addEvent(eventContents.stream().map(content -> {
            EventMessageDO message = new EventMessageDO();
            message.setEventKey(eventKey);
            message.setVersion(version);
            message.setContent(content);
            return message;
        }).collect(Collectors.toList()));
        return version;
    }

    @Nonnull
    @Override
    public List<EventMessage> queryMessages(@Nonnull String eventKey, @Nonnull Long version, boolean forceMaster) {
        return eventContentRepository.queryAfter(eventKey, version, forceMaster).stream().map(msg -> {
            EventMessage message = new EventMessage();
            message.setEventKey(msg.getEventKey());
            message.setContent(msg.getContent());
            message.setVersion(msg.getVersion());
            return message;
        }).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public Map<String, Long> queryLatestVersion(@Nonnull List<String> eventKey) {
        List<EventVersionDO> eventVersions = eventVersionRepository.queryLatestVersion(eventKey);
        return eventVersions.stream().collect(Collectors.toMap(EventVersionDO::getEventKey, EventVersionDO::getVersion));
    }
}
