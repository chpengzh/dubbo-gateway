package org.apache.dubbo.gateway.admin.constants;

import java.util.Objects;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public enum ApproveType {

    API_PUBLICATION(0, "接口发布审批"),

    UNKNOWN(-1, "未知审批流");

    private final int code;

    private final String title;

    ApproveType(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public static ApproveType fromCode(Integer code) {
        for (ApproveType type : values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
