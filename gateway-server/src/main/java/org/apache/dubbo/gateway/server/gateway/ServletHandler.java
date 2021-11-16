package org.apache.dubbo.gateway.server.gateway;

import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet 主处理流程
 *
 * @author chpengzh@foxmail.com
 * @date 2020/9/27 20:43
 */
public interface ServletHandler {

    /**
     * 将请求对象解析为业务请求上下文
     *
     * @param request 请求对象
     * @return 业务请求上下文
     */
    @Nonnull
    ApiContext readRequest(@Nonnull HttpServletRequest request) throws IOException;

    /**
     * 处理正常业务返回
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param context  请求上下文
     * @param result   响应结果
     */
    Void writeResult(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nullable ApiContext context,
            @Nonnull ApiResult result
    ) throws IOException;

    /**
     * 处理跨域请求返回
     *
     * @param response 响应对象
     */
    void handleCrossDomain(@Nonnull HttpServletResponse response);
}
