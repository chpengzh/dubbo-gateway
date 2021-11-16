package org.apache.dubbo.gateway.server.chain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
public class ApiResult {

    /**
     * 业务返回码
     */
    private int code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 响应数据体.
     */
    private Object data = new HashMap<String, Object>();

    /**
     * 日志记录的入参信息(问题定位用)
     */
    @JSONField(serialize = false, deserialize = false)
    private transient String requestMessage;

    /**
     * 日志记录的错误信息(问题定位用)
     */
    @JSONField(serialize = false, deserialize = false)
    private transient String responseMessage;

    public ApiResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
