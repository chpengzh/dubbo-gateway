package org.apache.dubbo.gateway.server.chain;

import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResult;

import javax.annotation.Nonnull;

/**
 * 业务请求响应流程
 */
public interface ApiPipelineHandler {

    /**
     * 执行逻辑链
     *
     * @param apiContext 请求上下文
     * @return 请求结果, 非空请求结果
     */
    <T extends Throwable> Void handle(@Nonnull ApiContext apiContext, Callback<T> callback) throws T;

    /**
     * 异步响应结果
     */
    interface Callback<T extends Throwable> {
        Void result(@Nonnull ApiResult result) throws T;
    }
}
