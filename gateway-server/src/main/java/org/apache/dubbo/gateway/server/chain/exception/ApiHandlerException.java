package org.apache.dubbo.gateway.server.chain.exception;

public class ApiHandlerException extends RuntimeException {

    private static final long serialVersionUID = 2866066350090072065L;

    private final ApiCode code;

    public ApiHandlerException(
            ApiCode code,
            String msg,
            Throwable cause
    ) {
        super(msg, cause);
        this.code = code;
    }

    public ApiHandlerException(
            ApiCode code,
            String msg
    ) {
        this(code, msg, null);
    }

    public ApiHandlerException(ApiCode code) {
        this(code, null, null);
    }

    public ApiHandlerException(String message) {
        this(ApiCode.GATEWAY_ERROR, message, null);
    }

    public ApiHandlerException(String message, Throwable cause) {
        this(ApiCode.GATEWAY_ERROR, message, cause);
    }

    public ApiCode getCode() {
        return code;
    }
}
