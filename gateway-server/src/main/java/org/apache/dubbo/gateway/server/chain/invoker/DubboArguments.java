package org.apache.dubbo.gateway.server.chain.invoker;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.dubbo.rpc.service.GenericService;

@Data
@AllArgsConstructor
final class DubboArguments {

    /**
     * 接口参数类型
     *
     * @see GenericService#$invoke(String, String[], Object[])
     */
    private final String[] invokeTypes;

    /**
     * 接口参数值
     *
     * @see GenericService#$invoke(String, String[], Object[])
     */
    private final Object[] invokeValues;
}