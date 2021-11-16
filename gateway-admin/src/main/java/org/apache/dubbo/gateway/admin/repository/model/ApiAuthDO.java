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
public class ApiAuthDO implements Serializable {
    private static final long serialVersionUID = -5247665087087180504L;

    private Long id;

    @Deprecated
    private Long apiId;

    private String appKey;

    private String apiName;

    private String apiVersion;

    private Long singleConCount;

    private Long updateTime;

    private String updater;
}
