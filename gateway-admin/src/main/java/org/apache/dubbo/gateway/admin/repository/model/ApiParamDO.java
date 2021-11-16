package org.apache.dubbo.gateway.admin.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Huang Haocheng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiParamDO implements Serializable {
    private static final long serialVersionUID = 8170506853101918404L;

    private Long id;

    private String uuid;

    private String parentUuid;

    private String apiName;

    private String sourceName;

    private Integer paramType;

    private String destName;

    private String defaultValue;

    private Integer required;

    private String className;
}
