package org.apache.dubbo.gateway.admin.utils;

import org.apache.dubbo.gateway.admin.controller.model.RpcResult;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

public class RpcResultUtil {

    private static final String DEFAULT_SUCC_MSG = "success";

    public static <T> RpcResult<T> createSuccessResult(T data) {
        return createSuccessResult(data, DEFAULT_SUCC_MSG);
    }

    public static <T> RpcResult<T> createSuccessResult(T data, String message) {
        RpcResult<T> result = new RpcResult<>();
        result.setCode(200);
        result.setMsg(message);
        result.setData(data);
        return result;
    }

    public static <T> RpcResult<T> createFailedResult(T failed, int errorCode, String msg) {
        RpcResult<T> result = new RpcResult<>();
        result.setCode(errorCode);
        result.setMsg(msg);
        result.setData(failed);
        return result;
    }

    public static <T> RpcResult<T> createFailedResult(int errorCode, String msg) {
        RpcResult<T> result = new RpcResult<>();
        result.setCode(errorCode);
        result.setMsg(msg);
        return result;
    }

    /**
     * 判断RPC请求是否失败
     * <p> 适用于RPC请求且有数据返回的情况</p>
     *
     * @param rpcResult 请求结果
     * @return {@code true} 如果失败
     */
    public static boolean isFailedOnData(RpcResult<?> rpcResult) {
        return isFailedWithoutData(rpcResult) || null == rpcResult.getData();
    }

    /**
     * 判断RPC请求是否失败
     * <p> 适用于RPC请求且无数据返回的情况</p>
     *
     * @param rpcResult 请求结果
     * @return {@code true} 如果失败
     */
    public static boolean isFailedWithoutData(RpcResult<?> rpcResult) {
        return null == rpcResult || 200 != rpcResult.getCode();
    }

    /**
     * 判断RPC请求是否失败
     * <p> 适用于RPC请求且有数据且数据为List结构数据</p>
     *
     * @param rpcResult 请求结果
     * @return {@code true} 如果失败
     */
    public static boolean isFailedWithList(RpcResult<?> rpcResult) {
        return isFailedOnData(rpcResult) || CollectionUtils.isEmpty((Collection<?>) rpcResult.getData());
    }
}