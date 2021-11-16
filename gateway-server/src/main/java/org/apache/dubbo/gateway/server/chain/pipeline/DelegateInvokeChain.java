package org.apache.dubbo.gateway.server.chain.pipeline;

import org.apache.dubbo.gateway.server.chain.ApiFilter;
import org.apache.dubbo.gateway.server.chain.ApiInvokeChain;
import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResult;
import org.apache.dubbo.gateway.server.chain.model.ApiResults;
import org.springframework.aop.support.AopUtils;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

final class DelegateInvokeChain implements ApiInvokeChain {

    private final ApiInvokeChain chain;

    private final ApiFilter aggregate;

    DelegateInvokeChain(ApiInvokeChain chain, ApiFilter aggregate) {
        this.chain = chain;
        this.aggregate = aggregate;
    }

    @Nonnull
    @Override
    public CompletableFuture<ApiResult> invoke(@Nonnull ApiContext context) {
        try {
            return aggregate.handle(context, chain);
        } catch (Throwable err) {
            return CompletableFuture.completedFuture(ApiResults.get(err));
        }
    }

    @Override
    public String toString() {
        return String.format("DelegateInvokeChain(filter='%s)", AopUtils.getTargetClass(aggregate).getSimpleName());
    }
}