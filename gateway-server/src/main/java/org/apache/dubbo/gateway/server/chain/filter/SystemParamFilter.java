package org.apache.dubbo.gateway.server.chain.filter;

import org.apache.dubbo.gateway.api.model.ApiInfo;
import org.apache.dubbo.gateway.server.chain.ApiFilter;
import org.apache.dubbo.gateway.server.chain.ApiInvokeChain;
import org.apache.dubbo.gateway.server.chain.exception.ApiCode;
import org.apache.dubbo.gateway.server.chain.exception.ApiHandlerException;
import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResult;
import org.apache.dubbo.gateway.server.repository.ApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * 平台参数预处理与合法性校验
 *
 * @author chpengzh@foxmail.com
 */
@Service
public class SystemParamFilter implements ApiFilter {

    @Autowired
    private ApiRepository apiRepo;

    @Nonnull
    @Override
    public CompletableFuture<ApiResult> handle(@Nonnull ApiContext context, @Nonnull ApiInvokeChain chain) {
        ApiInfo api = apiRepo.get(String.valueOf(context.getBizParams().get("api")));
        if (api == null) {
            throw new ApiHandlerException(ApiCode.API_NOT_FOUND);
        }
        context.setApi(api);
        return chain.invoke(context);
    }
}
