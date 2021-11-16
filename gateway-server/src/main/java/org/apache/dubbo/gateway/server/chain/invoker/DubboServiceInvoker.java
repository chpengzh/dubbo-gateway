package org.apache.dubbo.gateway.server.chain.invoker;

import org.apache.dubbo.gateway.api.constants.ApiParamType;
import org.apache.dubbo.gateway.api.model.ApiInfo;
import org.apache.dubbo.gateway.api.model.ApiInfo.ApiParam;
import org.apache.dubbo.gateway.server.chain.ApiFilter;
import org.apache.dubbo.gateway.server.chain.ApiInvokeChain;
import org.apache.dubbo.gateway.server.chain.exception.ApiCode;
import org.apache.dubbo.gateway.server.chain.exception.ApiHandlerException;
import org.apache.dubbo.gateway.server.chain.model.ApiContext;
import org.apache.dubbo.gateway.server.chain.model.ApiResult;
import org.apache.dubbo.gateway.server.chain.model.ApiResults;
import org.apache.dubbo.gateway.server.dubbo.DubboGenericFactory;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * dubbo参数转换
 *
 * @author chpengzh@foxmail.com
 */
@Service
public class DubboServiceInvoker implements ApiFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboServiceInvoker.class);

    @Autowired
    private DubboGenericFactory dubboGenericFactory;

    @Nonnull
    @Override
    public CompletableFuture<ApiResult> handle(@Nonnull ApiContext context, @Nonnull ApiInvokeChain chain) {
        ApiInfo api = context.getApi();
        DubboArguments arguments = parseArguments(context);
        GenericService service = getGenericService(api);
        return invokeAsync(service, context, arguments);
    }

    /**
     * Dubbo解析参数
     */
    private DubboArguments parseArguments(@Nonnull ApiContext apiContext) {
        List<ApiParam> apiParams = Optional
                .ofNullable(apiContext.getApi().getParams())
                .orElse(Collections.emptyList());
        String[] invokeTypes = new String[apiParams.size()];
        Object[] invokeValues = new Object[apiParams.size()];
        Map<String, Object> paramMap = apiContext.getBizParams();
        for (int i = 0; i < apiParams.size(); i++) {
            InvokeParam invokeParam = convertToInvokeParam(paramMap, apiParams.get(i));
            invokeTypes[i] = invokeParam.getType();
            invokeValues[i] = invokeParam.getValue();
        }
        return new DubboArguments(invokeTypes, invokeValues);
    }

    /**
     * 获取调用接口
     */
    private GenericService getGenericService(ApiInfo api) {
        try {
            return dubboGenericFactory.getOrCreateAsyncReference(
                    api.getServiceName(),
                    api.getServiceVersion());
        } catch (Throwable err) {
            throw new ApiHandlerException(ApiCode.GATEWAY_ERROR,
                    "[Can not found Service]"
                            + ";serviceName:" + api.getServiceName()
                            + ",serviceVersion:" + api.getServiceVersion(),
                    err);
        }
    }

    /**
     * Dubbo调用下游服务
     */
    private CompletableFuture<ApiResult> invokeAsync(
            GenericService service,
            ApiContext apiContext,
            DubboArguments dubboArguments
    ) {
        try {
            return service.$invokeAsync(apiContext.getApi().getMethod(),
                    dubboArguments.getInvokeTypes(),
                    dubboArguments.getInvokeValues()
            ).handleAsync(
                    (result, err) -> err == null
                            ? parseResult(result)
                            : ApiResults.get(parseError(err, apiContext)),
                    apiContext.getExecutor()
            );
        } catch (Throwable e) {
            throw parseError(e, apiContext);
        }
    }

    /***
     * 解析dubbo调用异常结果
     */
    private ApiHandlerException parseError(Throwable e, ApiContext apiContext) {
        String message = String.valueOf(e.getMessage());
        String apiHint = "api=" + apiContext.getApi().getApiName()
                + ",service=" + apiContext.getApi().getServiceName()
                + ",version=" + apiContext.getApi().getServiceVersion();
        if (message.contains("No provider available")) {
            return new ApiHandlerException(ApiCode.SUB_DUBBO_NO_PROVIDER_ERROR, message, e);
        }
        if (message.contains("NoSuchMethodException")) {
            return new ApiHandlerException(ApiCode.SUB_DUBBO_NO_SUCHMETHOD_ERROR, message, e);
        }
        if (message.contains("ClassNotFoundException")) {
            return new ApiHandlerException(ApiCode.SUB_DUBBO_NO_CLASS_FOUND_ERROR, message, e);
        }
        if (message.contains("Thread pool is EXHAUSTED")) {
            // 提供者线程池满, 请求拒绝
            return new ApiHandlerException(ApiCode.SUB_DUBBO_THREAD_POOL_EXHAUSTED_ERROR, apiHint);
        }
        if (message.contains("Waiting server-side response timeout by scan timer")) {
            // 调用超时, 提供者响应时间超过4s
            return new ApiHandlerException(ApiCode.SERVICE_INVOKE_TIMEOUT, apiHint);
        }
        // 通常为提供者未捕获异常
        return new ApiHandlerException(ApiCode.BIZ_DUBBO_SERVICE_ERROR, message, e);
    }

    /**
     * 解析Dubbo调用业务返回结果
     */
    private ApiResult parseResult(Object invokeResult) {
        Map<String, Object> resultMap;
        int code;
        Object data;
        String msg;
        if (invokeResult == null) {
            return ApiResults.get(ApiCode.BIZ_DUBBO_RESPONSE_FORMAT_ERROR,
                    "[Invalid dubbo service response]dubbo invoke result is null");
        }

        if (!(invokeResult instanceof Map)) {
            return ApiResults.get(ApiCode.BIZ_DUBBO_RESPONSE_FORMAT_ERROR,
                    "[Invalid dubbo service response]dubbo invoke result is not RpcResult" +
                            ",current type:" + invokeResult.getClass().getSimpleName());
        }

        //noinspection unchecked
        resultMap = (Map<String, Object>) invokeResult;
        Object codeObj = resultMap.get("code");
        if (codeObj == null) {
            return ApiResults.get(ApiCode.BIZ_DUBBO_RESPONSE_FORMAT_ERROR,
                    "[Invalid dubbo service response]code was null");
        }

        if (codeObj instanceof Integer) {
            code = (Integer) codeObj;
        } else {
            try {
                code = Integer.parseInt(codeObj.toString());
            } catch (NumberFormatException e) {
                return ApiResults.get(ApiCode.BIZ_DUBBO_RESPONSE_FORMAT_ERROR,
                        "[Invalid dubbo service response]code is not number,current value:" + codeObj);
            }
        }

        //取出数据和描述
        data = resultMap.get("data");
        msg = (String) resultMap.get("msg");

        //删除data里的class属性
        try {
            data = RpcDataUtils.fixRpcResultClass(data);
        } catch (Throwable e) {
            LOGGER.warn("[Invalid dubbo service response]Remove 'class' error", e);
        }
        return new ApiResult(code, msg, data);
    }

    /**
     * 请求参数转化过程
     *
     * @param source 请求数据源source
     * @param param  待解析参数
     * @return dubbo入参解析结果
     */
    private InvokeParam convertToInvokeParam(Map<String, Object> source, ApiParam param) throws ApiHandlerException {
        if (ApiParamType.getByCode(param.getParamType()) == ApiParamType.OBJECT) {
            // 对象类型，需要对对象的类名进行合法性校验(控制后台需要保障一定要输入合法的类名)
            if (!ObjectUtils.isEmpty(param.getClassName())) {
                throw new ApiHandlerException(ApiCode.GATEWAY_ERROR,
                        "【非法的参数类型】" + param.getSourceName() + "缺少对应的类声明");
            }
            Map<String, Object> properties = new HashMap<>();
            for (ApiParam field : Optional.ofNullable(param.getParams()).orElse(Collections.emptyList())) {
                InvokeParam invokeParam = convertToInvokeParam(source, field);
                properties.put(field.getDestName(), invokeParam.getValue());
            }
            return new InvokeParam(param.getClassName(), properties);
        }

        // 原生数据类型, 如果为空，根据请求参数类型处理默认值
        Object httpValue = source.get(param.getSourceName());
        if (httpValue == null) {
            if (param.getParamType() == ApiParamType.FILE.getCode()) {
                httpValue = new byte[0];
            } else {
                // 如果为空，使用类型兼容的默认值
                httpValue = param.getDefaultValue();
                if (ObjectUtils.isEmpty(httpValue)) {
                    if (param.getParamType() >= 10 && param.getParamType() <= 16) {
                        httpValue = 0; // 基本类型
                    } else if (param.getParamType() == 17) {
                        httpValue = false; // 布尔类型
                    } else if (param.getParamType() >= 20 && param.getParamType() <= 27) {
                        httpValue = null; // 包装类型
                    }
                }
            }
        }

        // 得到入参类型
        String invokeType = ApiParamTypeCache.INSTANCE.getClassName(param.getParamType());
        if (invokeType == null) {
            throw new ApiHandlerException(ApiCode.GATEWAY_ERROR,
                    "【非法的参数类型】" + param.getSourceName() + "非法的参数类型:" + param.getParamType());
        }

        // 得到入参实际值
        try {
            Object invokeValue = RpcDataUtils.convert(httpValue, param.getSourceName(), param.getParamType());
            return new InvokeParam(invokeType, invokeValue);
        } catch (Throwable e) {
            throw new ApiHandlerException(ApiCode.INVALID_REQUEST,
                    "【参数转化错误】" + param.getSourceName() + ", err: " + e.getMessage());
        }
    }
}
