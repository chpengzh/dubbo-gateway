package org.apache.dubbo.gateway.admin.controller.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author chpengzh@foxmail.com
 */
@Data
@NoArgsConstructor
public class ApiSystemParamVO implements Serializable {

    private static final int serialVersionUID = 0x11;

    private String name;

    private Integer paramType;

    private String description;

    private boolean sessionParam;

    public ApiSystemParamVO(String name, Integer paramType, String description, boolean sessionParam) {
        this.name = name;
        this.paramType = paramType;
        this.description = description;
        this.sessionParam = sessionParam;
    }
}
