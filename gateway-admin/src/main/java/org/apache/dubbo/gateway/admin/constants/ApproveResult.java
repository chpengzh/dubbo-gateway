package org.apache.dubbo.gateway.admin.constants;

import java.util.Objects;

/**
 * @author chpengzh@foxmail.com
 */
public enum ApproveResult {

    /**
     * 审批中
     */
    STARTED(0),

    /**
     * 审批通过
     */
    APPROVED(1),

    /**
     * 审批回绝
     */
    REJECTED(2),

    /**
     * 未知状态
     */
    UNKNOWN(-1);

    private final int code;

    ApproveResult(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ApproveResult fromCode(Integer code) {
        for (ApproveResult result : values()) {
            if (Objects.equals(code, result.getCode())) {
                return result;
            }
        }
        return UNKNOWN;
    }
}
