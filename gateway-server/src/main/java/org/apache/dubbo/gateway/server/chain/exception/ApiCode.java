package org.apache.dubbo.gateway.server.chain.exception;

/**
 * 网关的响应状态编码
 *
 * @author wangxiaoxue
 * @version $Id: ApiCode.java, v 0.1 2015年4月8日 下午3:12:19 Administrator Exp $
 */
public enum ApiCode {

    SUCCESS(200, "OK"),

    GATEWAY_ERROR(999000, "系统开小差啦，请稍后重试"),

    API_NOT_FOUND(999200, "【参数错误】非法的接口名称"),

    INVALID_REQUEST(999301, "系统开小差啦，请稍后重试"),

    SERVICE_INVOKE_TIMEOUT(999400, "【提供者超时】提供者响应超时，请稍后重试"),

    SUB_DUBBO_NO_PROVIDER_ERROR(999401, "【无提供者】检查提供者是否正常启动，接口配置使用全限定名, version是否正确配置"),

    SUB_DUBBO_NO_SUCHMETHOD_ERROR(999402, "【调用方法错误】检查调用参数(顺序、数量、类型)是否与提供者方法匹配"),

    SUB_DUBBO_NO_CLASS_FOUND_ERROR(999403, "【调用类声明错误】检查提供者API版本与消费者版本是否一致,以及调用参数类名"),

    SUB_DUBBO_THREAD_POOL_EXHAUSTED_ERROR(999404, "【提供者繁忙】服务提供者线程池满, 请求拒绝"),

    BIZ_DUBBO_SERVICE_ERROR(999405, "【提供者错误】提供者未捕获异常"),

    BIZ_DUBBO_RESPONSE_FORMAT_ERROR(999406, "系统开小差啦，请稍后重试");

    private final int code;

    private final String msg;

    ApiCode(int code, String msg) {
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
