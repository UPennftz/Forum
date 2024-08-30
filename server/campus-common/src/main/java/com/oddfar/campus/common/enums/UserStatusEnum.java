package com.oddfar.campus.common.enums;

/**
 * 用户状态
 *
 */
public enum UserStatusEnum {
    OK("0", "normal"), DISABLE("1", "deactivate"), DELETED("2", "delete");

    private final String code;
    private final String info;

    UserStatusEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
