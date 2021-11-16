package org.apache.dubbo.gateway.server.chain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.dubbo.gateway.api.model.ApiInfo;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * 网关请求上下文
 *
 * @author chpengzh@foxmail.com
 */
@ThreadSafe
@NoArgsConstructor
public class ApiContext {

    /**
     * 执行线程池
     */
    @Getter
    @Setter
    private volatile ExecutorService executor;

    /**
     * api信息
     */
    @Getter
    @Setter
    private volatile ApiInfo api;

    /**
     * 请求参数
     */
    private final Map<String, Object> bizParams = new ConcurrentHashMap<>();

    /**
     * 获取业务参数列表
     */
    public Map<String, Object> getBizParams() {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : bizParams.entrySet()) {
            if (entry.getValue() == Default.nil) {
                result.put(entry.getKey(), null);
            } else {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * 写入业务参数
     *
     * @param params 设置params
     */
    public void putBizParams(@Nonnull Map<String, ?> params) {
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            putBizParam(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 写入业务参数
     *
     * @param key   设置key
     * @param value 设置value
     */
    public void putBizParam(String key, Object value) {
        bizParams.put(key, value == null
                ? Default.nil
                : value);
    }

    /**
     * 空值的placeholder
     */
    private enum Default {
        nil,
    }
}
