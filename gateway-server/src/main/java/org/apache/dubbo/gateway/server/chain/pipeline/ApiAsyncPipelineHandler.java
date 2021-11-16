package org.apache.dubbo.gateway.server.chain.pipeline;

import org.apache.dubbo.gateway.server.chain.ApiFilter;
import org.apache.dubbo.gateway.server.chain.ApiInvokeChain;
import org.apache.dubbo.gateway.server.chain.ApiPipelineHandler;
import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步执行的调用链逻辑
 *
 * @author chpengzh@foxmail.com
 */
public class ApiAsyncPipelineHandler implements ApiPipelineHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiAsyncPipelineHandler.class);

    private final ThreadPoolExecutor executorService;

    private final ApiInvokeChain chain;

    public ApiAsyncPipelineHandler(int core, int max, @Nonnull List<ApiFilter> handlers) {
        this.executorService = createExecutor(core, max);
        Collections.reverse(handlers);
        ApiInvokeChain next = new EmptyInvokeChain();
        for (ApiFilter handler : handlers) {
            next = new DelegateInvokeChain(next, handler);
        }
        this.chain = next;
    }

    public ApiAsyncPipelineHandler(ThreadPoolExecutor executorService, @Nonnull List<ApiFilter> handlers) {
        this.executorService = executorService;
        Collections.reverse(handlers);
        ApiInvokeChain next = new EmptyInvokeChain();
        for (ApiFilter handler : handlers) {
            next = new DelegateInvokeChain(next, handler);
        }
        this.chain = next;
    }

    /**
     * 创建工作线程池
     *
     * @param core 核心线程池
     * @param max  最大线程池
     * @return 工作线程池
     */
    private ThreadPoolExecutor createExecutor(int core, int max) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                core,
                max,
                1,
                TimeUnit.DAYS,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.AbortPolicy());
        executor.prestartAllCoreThreads();
        executor.allowCoreThreadTimeOut(false);
        return executor;
    }

    @Override
    public <T extends Throwable> Void handle(@Nonnull ApiContext apiContext, Callback<T> callback) throws T {
        apiContext.setExecutor(executorService);

        Callback<T> internal = new IdempotentCallback<>(callback);
        try {
            executorService.submit(() -> {
                chain.invoke(apiContext).whenComplete((result, err) -> {
                    try {
                        if (err != null) {
                            internal.result(ApiResults.get(err));
                        } else {
                            internal.result(result);
                        }
                    } catch (Throwable unexpected) {
                        LOGGER.error("Unexpected error.", unexpected);
                    }
                });
            });
            return null;
        } catch (Throwable err) {
            return internal.result(ApiResults.get(err));
        }
    }
}
