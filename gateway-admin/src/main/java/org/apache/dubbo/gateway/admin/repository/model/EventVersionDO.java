package org.apache.dubbo.gateway.admin.repository.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/13 13:54
 */
@Data
public class EventVersionDO implements Serializable {

    private static final int serialVersionUID = 0x11;

    private Long id;

    private String eventKey;

    private Long version;

}
