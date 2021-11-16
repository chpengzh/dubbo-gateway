package org.apache.dubbo.gateway.server.chain.invoker;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
final class InvokeParam {

    /**
     * 参数类型
     */
    private final String type;

    /**
     * 参数值
     */
    private final Object value;

}