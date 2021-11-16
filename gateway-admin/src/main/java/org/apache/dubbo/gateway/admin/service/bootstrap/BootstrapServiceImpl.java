package org.apache.dubbo.gateway.admin.service.bootstrap;

import org.apache.dubbo.gateway.admin.service.api.ApiReleaseService;
import org.apache.dubbo.gateway.admin.service.model.ApiQueryBO;
import org.apache.dubbo.gateway.api.model.ApiInfo;
import org.apache.dubbo.gateway.api.service.BootstrapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Service("bootstrapService")
public class BootstrapServiceImpl implements BootstrapService {

    @Autowired
    private ApiReleaseService apiReleaseService;

    @Override
    public List<ApiInfo> getApis(int limit, int offset) {
        ApiQueryBO query = new ApiQueryBO();
        query.setOffset(offset);
        query.setLimit(limit);
        return apiReleaseService.query(query);
    }
}
