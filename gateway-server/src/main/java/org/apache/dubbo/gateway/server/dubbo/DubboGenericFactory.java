package org.apache.dubbo.gateway.server.dubbo;

import org.apache.dubbo.rpc.service.GenericService;

import javax.annotation.Nonnull;

/**
 * Dubbo泛化调用服务
 *
 * @author chpengzh@foxmail.com
 * @date 2020-08-09 17:41
 */
public interface DubboGenericFactory {

    /**
     * 创建调用服务引用
     *
     * @param serviceName    接口服务名称
     * @param serviceVersion 接口服务版本号
     * @return 服务引用实例
     */
    @Nonnull
    GenericService getOrCreateAsyncReference(@Nonnull String serviceName, @Nonnull String serviceVersion);

    /**
     * 回收调用服务引用
     *
     * @param serviceName    接口服务名称
     * @param serviceVersion 接口服务版本号
     */
    void deleteReference(
            @Nonnull String serviceName,
            @Nonnull String serviceVersion
    );

}
