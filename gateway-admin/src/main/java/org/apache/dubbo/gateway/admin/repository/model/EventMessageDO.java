package org.apache.dubbo.gateway.admin.repository.model;

import lombok.Data;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 12:49
 */
@Data
public class EventMessageDO {

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
