package org.apache.dubbo.gateway.server.chain.model;

import org.apache.dubbo.gateway.server.chain.exception.ApiCode;
import org.apache.dubbo.gateway.server.chain.exception.ApiHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * API错误提示的缓存
 *
 * @author wangxiaoxue
 * @version $Id: ApiResultCache.java, v 0.1 2015年4月8日 下午3:25:44 Administrator Exp $
 */
public class ApiResults {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiResults.class);

    /**
     * 响应最大截取长度
     */
    private static final int MAX_LENGTH = 10_000;

    public static ApiResult get(@Nonnull Throwable e) {
        if (e instanceof ApiHandlerException) {
            ApiHandlerException err = (ApiHandlerException) e;
            return ApiResults.get(err.getCode(), err.getMessage(), err.getCause());
        } else {
            LOGGER.error("Gateway unexpected error", e);
            return ApiResults.get(ApiCode.GATEWAY_ERROR, e.getMessage(), e);
        }
    }

    public static ApiResult get(@Nonnull ApiCode apiCode) {
        return get(apiCode, null, null);
    }

    public static ApiResult get(@Nonnull ApiCode apiCode, @Nullable String detail) {
        return get(apiCode, detail, null);
    }

    public static ApiResult get(
            @Nonnull ApiCode apiCode,
            @Nullable String appendMsg,
            @Nullable Throwable e
    ) {
        ApiResult result = new ApiResult();
        result.setCode(apiCode.getCode());
        result.setResponseMessage(generateResponseMessage(apiCode, appendMsg, e));
        result.setMsg(result.getResponseMessage());
        return result;
    }

    /**
     * 生成响应值消息
     *
     * @param apiCode   响应码
     * @param appendMsg 扩展消息
     * @param e         错误信息
     * @return 响应消息
     */
    private static String generateResponseMessage(
            @Nonnull ApiCode apiCode,
            @Nullable String appendMsg,
            @Nullable Throwable e
    ) {
        StringBuilder builder = new StringBuilder(apiCode.getMsg()).append(";");
        if (!ObjectUtils.isEmpty(appendMsg)) {
            builder.append(appendMsg);
        }
        if (e != null) {
            builder.append(e);
        }
        if (builder.length() > MAX_LENGTH) {
            builder.setLength(MAX_LENGTH);
        }
        return builder.toString();
    }

}
