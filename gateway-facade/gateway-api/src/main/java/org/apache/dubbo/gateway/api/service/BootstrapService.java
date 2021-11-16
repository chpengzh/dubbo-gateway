package org.apache.dubbo.gateway.api.service;

import org.apache.dubbo.gateway.api.model.ApiInfo;

import java.util.List;

/**
 * @author chpengzh@foxmail.com
 */
public interface BootstrapService {

    /**
     * 分页获取API信息
     */
    List<ApiInfo> getApis(int limit, int offset);

}
