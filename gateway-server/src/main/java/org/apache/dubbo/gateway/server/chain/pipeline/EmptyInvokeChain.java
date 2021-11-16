package org.apache.dubbo.gateway.server.chain.pipeline;

import org.apache.dubbo.gateway.server.chain.ApiInvokeChain;
import org.apache.dubbo.gateway.server.chain.exception.ApiCode;
import org.apache.dubbo.gateway.server.chain.exception.ApiHandlerException;
import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResult;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

final class EmptyInvokeChain implements ApiInvokeChain {

    @Nonnull
    @Override
    public CompletableFuture<ApiResult> invoke(@Nonnull ApiContext context) throws ApiHandlerException {
        throw new ApiHandlerException(ApiCode.GATEWAY_ERROR,
                "[Do handler error]All handler execute finished,but apiResult was null");
    }

    @Override
    public String toString() {
        return "EmptyInvokeChain";
    }
}

