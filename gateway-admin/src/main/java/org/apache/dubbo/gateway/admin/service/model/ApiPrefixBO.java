package org.apache.dubbo.gateway.admin.service.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chpengzh@foxmail.com
 */
@Data
public class ApiPrefixBO implements Serializable {

    private static final int serialVersionUID = 0x11;

    /**
     * 接口前缀标识
     */
    private String prefix;

    /**
     * 业务线名称
     */
    private String name;

}
