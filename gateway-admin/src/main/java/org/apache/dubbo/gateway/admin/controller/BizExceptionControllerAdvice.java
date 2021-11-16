package org.apache.dubbo.gateway.admin.controller;

import org.apache.dubbo.gateway.admin.exception.RpcBizException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chpengzh@foxmail.com
 */
@ControllerAdvice
public class BizExceptionControllerAdvice {

    /**
     * 普通业务异常
     *
     * @param err 异常对象
     * @return 响应结果
     */
    @ExceptionHandler(RpcBizException.class)
    public ResponseEntity<Map<String, Object>> handleError(RpcBizException err) {
        return new ResponseEntity<>(new HashMap<String, Object>() {{
            put("code", err.getCode());
            put("msg", err.getMsg());
        }}, HttpStatus.BAD_REQUEST);
    }

}
