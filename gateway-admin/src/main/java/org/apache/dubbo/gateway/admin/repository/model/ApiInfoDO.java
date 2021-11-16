package org.apache.dubbo.gateway.admin.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Huang Haocheng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiInfoDO implements Serializable {

    private static final long serialVersionUID = 1032238609104669664L;

    private Long id;

    private String apiName;

    private String serviceName;

    private String serviceVersion;

    private String serviceGroup;

    private String method;

}
