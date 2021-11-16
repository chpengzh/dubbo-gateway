package org.apache.dubbo.gateway.server.dubbo.impl;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.gateway.server.dubbo.DubboGenericFactory;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chpengzh@foxmail.com
 * @date 2020-08-09 17:46
 */
@Service
public class DubboGenericFactoryImpl implements DubboGenericFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboGenericFactory.class);

    private final Map<String, ReferenceConfigHolder> reference = new ConcurrentHashMap<>();

    @Resource
    private List<RegistryConfig> registryConfigs;

    @Resource
    private ConsumerConfig consumerConfig;

    @Nonnull
    @Override
    public GenericService getOrCreateAsyncReference(@Nonnull String serviceName, @Nonnull String serviceVersion) {
        return prepareReferenceIfNotExists(serviceName, serviceVersion, true).get();
    }

    @Override
    public void deleteReference(@Nonnull String serviceName, @Nonnull String serviceVersion) {
        for (boolean async : new boolean[]{true, false}) {
            ReferenceConfigHolder holder = reference.remove(uniqKey(serviceName, serviceVersion, async));
            if (holder != null) {
                String refName = String.valueOf(holder.getRef());
                try {
                    holder.destroy();
                } catch (Throwable err) {
                    LOGGER.error(" [DUBBO] Destroy dubbo reference fail: " + refName, err);
                }
            }
        }
    }

    /**
     * 创建或新增引用
     *
     * @param serviceName    服务名称
     * @param serviceVersion 服务版本号
     * @return 连接引用
     */
    @Nonnull
    private ReferenceConfigHolder prepareReferenceIfNotExists(
            @Nonnull String serviceName,
            @Nonnull String serviceVersion,
            boolean async
    ) {
        return reference.computeIfAbsent(uniqKey(serviceName, serviceVersion, async), (key) -> {
            try {
                ReferenceConfig<GenericService> newReference = new ReferenceConfig<>();
                newReference.setInterface(serviceName);
                newReference.setVersion(serviceVersion);
                newReference.setGeneric(Boolean.TRUE.toString());
                newReference.setCheck(false);
                newReference.setAsync(async);
                newReference.setRegistries(registryConfigs);
                newReference.setConsumer(consumerConfig);
                return new ReferenceConfigHolder(newReference);
            } catch (Throwable err) {
                LOGGER.error(" [DUBBO] Create dubbo reference fail" +
                        ",serviceName=" + serviceName +
                        ",serviceVersion=" + serviceVersion, err);
                throw err;
            }
        });
    }

    private String uniqKey(String serviceName, String version, boolean async) {
        return serviceName + ":" + version + ":" + async;
    }

    static class ReferenceConfigHolder implements GenericService {

        final ReferenceConfig<GenericService> ref;

        final GenericService service;

        /**
         * 这里持有 ReferenceConfig 引用，是为了防止被回收导致内存泄漏
         */
        ReferenceConfigHolder(ReferenceConfig<GenericService> ref) {
            this.ref = ref;
            this.service = ref.get();
        }

        GenericService get() {
            return this;
        }

        ReferenceConfig<GenericService> getRef() {
            return ref;
        }

        void destroy() {
            ref.destroy();
        }

        @Override
        public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
            return service.$invoke(method, parameterTypes, args);
        }

        @Override
        public CompletableFuture<Object> $invokeAsync(
                String method,
                String[] parameterTypes,
                Object[] args
        ) throws GenericException {
            service.$invoke(method, parameterTypes, args);
            if (RpcContext.getContext().getCompletableFuture() != null) {
                return RpcContext.getContext().getCompletableFuture();
            }
            throw new GenericException(ReferenceConfigHolder.class.getName(), "Can't find generic service");
        }
    }
}
