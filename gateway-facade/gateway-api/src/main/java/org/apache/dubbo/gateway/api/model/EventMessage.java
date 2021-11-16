package org.apache.dubbo.gateway.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 同步事件消息
 *
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 11:33
 */
@Data
public class EventMessage implements Serializable {

    private static final int serialVersionUID = 0x11;

    /**
     * 事件类型
     */
    private String eventKey;

    /**
     * 事件版本号
     */
    private Long version;

    /**
     * 事件消息体
     */
    private String content;

}
