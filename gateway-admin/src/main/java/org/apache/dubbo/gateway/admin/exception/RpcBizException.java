package org.apache.dubbo.gateway.admin.exception;

/**
 * 所有对外暴露Dubbo接口的异常父类
 */
public class RpcBizException extends RuntimeException {

    private static final long serialVersionUID = 5195457096898732126L;

    public static final String DEFAULT_MSG = "默认系统错误";

    private int code = 200;

    private String msg = DEFAULT_MSG;

    public RpcBizException() {
        super();
    }

    public RpcBizException(Throwable cause) {
        super(cause);
    }

    public RpcBizException(int code) {
        super(DEFAULT_MSG);
        this.code = code;
    }

    public RpcBizException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public RpcBizException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
