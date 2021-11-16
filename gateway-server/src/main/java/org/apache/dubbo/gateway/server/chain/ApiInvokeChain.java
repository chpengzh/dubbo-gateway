package org.apache.dubbo.gateway.server.chain;

import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResult;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * 请求执行调用链
 *
 * @author chpengzh@foxmail.com
 * @date 2020/10/9 18:12
 */
public interface ApiInvokeChain {

    /**
     * 异步执行链
     *
     * @param context 请求上下文
     * @return 响应结果, 执行过程不抛出任何异常
     */
    CompletableFuture<ApiResult> invoke(@Nonnull ApiContext context);
}
