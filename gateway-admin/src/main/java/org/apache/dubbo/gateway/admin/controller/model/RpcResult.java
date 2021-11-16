package org.apache.dubbo.gateway.admin.controller.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResult<T> implements Serializable {

    private static final long serialVersionUID = -4943729766013204932L;

    private int code = -1;

    private String msg;

    private T data;

}