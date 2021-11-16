package org.apache.dubbo.gateway.admin.constants;

import org.apache.dubbo.gateway.admin.exception.RpcBizException;

/**
 * @author chpengzh@foxmail.com
 * @date 2021/2/2 12:01
 */
public enum ErrorCode {

    ILLEGAL_ARGS(998000, "请求参数错误"),

    SESSION_NOT_AUTHORIZED(998001, "用户未登录"),

    APPROVE_ILLEGAL_STATE(998002, "审批状态异常"),

    API_PUBLISH_ERROR(998003, "服务发布错误"),

    PUBLISH_EMPTY_MESSAGE(998004, "发布消息为空"),

    ODIN_CREATE_ERROR(998005, "Odin图表创建错误");

    private final int code;

    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RpcBizException abort() {
        return new RpcBizException(code, msg);
    }

    public RpcBizException abort(String msg) {
        return new RpcBizException(code, this.msg + "," + msg);
    }
}
