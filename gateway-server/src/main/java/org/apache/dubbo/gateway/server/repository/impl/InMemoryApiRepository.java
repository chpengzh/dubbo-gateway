package org.apache.dubbo.gateway.server.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.gateway.api.constants.EventName;
import org.apache.dubbo.gateway.api.model.ApiInfo;
import org.apache.dubbo.gateway.api.service.BootstrapService;
import org.apache.dubbo.gateway.server.dubbo.DubboGenericFactory;
import org.apache.dubbo.gateway.server.repository.ApiRepository;
import org.apache.dubbo.gateway.server.repository.sync.ApiReadyEvent;
import org.apache.dubbo.gateway.server.repository.sync.ConfigChangedResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 内存中的API存储
 *
 * @author chpengzh@foxmail.com
 * @date 2020-09-24 10:35
 */
@Repository
public class InMemoryApiRepository implements ApiRepository, ConfigChangedResolver, ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryApiRepository.class);

    private final Map<String, ApiInfo> items = new ConcurrentHashMap<>();

    @Resource
    private BootstrapService bootstrapService;

    @Autowired
    private DubboGenericFactory dubboGenericFactory;

    @Override
    public ApiInfo get(@Nullable String apiName) {
        return items.get(apiName);
    }

    @Override
    public Collection<ApiInfo> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public void createOrUpdate(ApiInfo api) {
        items.compute(api.getApiName(), (key, originApi) -> {
            updateDubboApi(originApi, api);
            LOGGER.info("[InMemoryApiRepository] createOrUpdate:{}", JSON.toJSONString(api));
            return api;
        });
    }

    @Override
    public void delete(String apiName) {
        items.computeIfPresent(apiName, (key, originApi) -> {
            deleteDubboApi(originApi);
            LOGGER.info("[InMemoryApiRepository] delete:{}", JSON.toJSONString(originApi));
            return null;
        });
    }

    private void updateDubboApi(ApiInfo originApi, ApiInfo newApi) {
        try {
            if (originApi != null) {
                // 执行服务名变更时要销毁旧引用
                dubboGenericFactory.deleteReference(originApi.getServiceName(), originApi.getServiceVersion());
            }
            // 创建一个新引用
            dubboGenericFactory.getOrCreateAsyncReference(newApi.getServiceName(), newApi.getServiceVersion());
        } catch (Throwable ignore) {
        }
    }

    private void deleteDubboApi(ApiInfo originApi) {
        try {
            // 当下线API时主动销毁引用
            dubboGenericFactory.deleteReference(originApi.getServiceName(), originApi.getServiceVersion());
        } catch (Throwable ignore) {

        }
    }


    @Override
    public void resolveChange(EventName eventType, String text, JSONObject event) {
        switch (eventType) {
            case API_UPGRADE: {
                ApiInfo api = JSON.parseObject(text, ApiInfo.class);
                createOrUpdate(api);
                break;
            }
            case API_OFFLINE: {
                ApiInfo api = JSON.parseObject(text, ApiInfo.class);
                delete(api.getApiName());
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * 执行并发初始化，早点下班回家陪老婆!!!
     *
     * @param parallel 并发数量
     * @param tasks    并发任务
     * @throws Exception 任务异常
     */
    private void parallelRun(int parallel, List<Runnable> tasks) {
        ExecutorService executorService = Executors.newFixedThreadPool(parallel);
        List<Future<Void>> initResult = new ArrayList<>();
        try {
            tasks.forEach(task -> initResult.add(executorService.submit(() -> {
                task.run();
                return null;
            })));
            for (Future<Void> future : initResult) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    throw e.getCause() instanceof Exception
                            ? new RuntimeException(e.getCause())
                            : new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationReadyEvent event) {
        int limit = 50, offset = 0;
        Map<String, ApiInfo> result = new HashMap<>();
        List<ApiInfo> page;
        while (!CollectionUtils.isEmpty(page = bootstrapService.getApis(limit, offset))) {
            offset += page.size();
            page.forEach(next -> result.put(next.getApiName(), next));
        }
        LOGGER.info("[InMemoryApiRepository] init api count={}", result.size());
        parallelRun(2 * Runtime.getRuntime().availableProcessors() + 1,
                result.values().stream()
                        .<Runnable>map(ApiInfo -> () -> createOrUpdate(ApiInfo))
                        .collect(Collectors.toList()));
        event.getApplicationContext().publishEvent(new ApiReadyEvent(event.getApplicationContext()));
    }
}

