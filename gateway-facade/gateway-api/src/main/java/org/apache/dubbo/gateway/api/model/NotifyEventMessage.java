package org.apache.dubbo.gateway.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 事件更新消息
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/10 18:02
 */
@Data
public class NotifyEventMessage implements Serializable {

    private static final int serialVersionUID = 0x11;

    /**
     * 消息trace
     */
    private String traceId;

    /**
     * 消息span
     */
    private String spanId;

    /**
     * 当前最新监听事件版本号
     * {@link Map.Entry#getKey()} 事件 event_key
     * {@link Map.Entry#getValue()} 事件版本号
     */
    private Map<String, Long> eventVersions;

    /**
     * 拉取增量事件消息体
     */
    private List<EventMessage> messages;

}
