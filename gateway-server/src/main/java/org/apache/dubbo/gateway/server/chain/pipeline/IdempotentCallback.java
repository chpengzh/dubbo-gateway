package org.apache.dubbo.gateway.server.chain.pipeline;

import org.apache.dubbo.gateway.server.chain.ApiPipelineHandler;
import org.apache.dubbo.gateway.server.chain.model.ApiResult;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;

final class IdempotentCallback<T extends Throwable> implements ApiPipelineHandler.Callback<T> {

    private final ApiPipelineHandler.Callback<T> callback;

    private final AtomicBoolean hasReturn = new AtomicBoolean();

    IdempotentCallback(ApiPipelineHandler.Callback<T> callback) {
        this.callback = callback;
    }

    @Override
    public Void result(@Nonnull ApiResult result) throws T {
        if (hasReturn.compareAndSet(false, true)) {
            return callback.result(result);
        }
        return null;
    }
}