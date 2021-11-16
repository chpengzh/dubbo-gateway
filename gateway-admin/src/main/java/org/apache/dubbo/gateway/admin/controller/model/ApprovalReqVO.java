package org.apache.dubbo.gateway.admin.controller.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Data
public class ApprovalReqVO implements Serializable {

    private static final int serialVersionUID = 0x11;

    /**
     * 审批流id
     */
    private String processId;

    /**
     * 审批记录
     */
    private String payload;

}
