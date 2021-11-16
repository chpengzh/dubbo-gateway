package org.apache.dubbo.gateway.server.chain;

import org.apache.dubbo.gateway.server.chain.exception.ApiHandlerException;
import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResult;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * 请求执行拦截器
 *
 * @author chpengzh@foxmail.com
 * @date 2020/10/9 18:44
 */
public interface ApiFilter {

    /**
     * 请求执行拦截器
     *
     * @param context 请求上下文
     * @param chain   执行调用链
     * @return 请求结果
     * @throws ApiHandlerException 执行异常
     */
    @Nonnull
    CompletableFuture<ApiResult> handle(
            @Nonnull ApiContext context,
            @Nonnull ApiInvokeChain chain
    ) throws ApiHandlerException;
}
