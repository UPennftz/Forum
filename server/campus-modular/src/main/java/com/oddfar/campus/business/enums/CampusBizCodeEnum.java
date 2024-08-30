package com.oddfar.campus.business.enums;

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *      002：短信验证码频率太高
 *  11: 用户
 *  12: 校园墙
 *  13: 评论
 *
 */
public enum CampusBizCodeEnum {

    /**
     * 系统通用
     */
    UNKNOWN_EXCEPTION(10000, "Unknown system error"),
    VALID_EXCEPTION(10001, "Parameter verification exception"),
    SMS_CODE_EXCEPTION(10002, "The verification code acquisition frequency is too high, please try again later"),
    TOO_MANY_REQUEST(10003, "The request traffic is too large, please try again later"),
    TOO_MANY_File(10004, "The number of uploaded files exceeds the limit, please upload tomorrow"),
    CODE_ERROR(10005, "Verification code error"),

    NO_THREE_CATEGORY(10010, "It is forbidden to add third-level classification"),
    CATEGORY_NAME_REPEAT(10011, "Duplicate category name"),
    CATEGORY_SLUG_REPEAT(10012, "Duplicate category abbreviation"),
    CATEGORY_NOT_EXIST(10013, "Category does not exist"),

    TAG_NAME_REPEAT(10021, "Duplicate label abbreviation"),

    /**
     * 信息墙
     */
    CONTENT_IS_NULL(12001,"The information wall content does not exist"),
    CONTENT_OPERATION_PROHIBITED(12002,"Information wall prohibited operations"),
    CONTENT_NOT_NULL(12003,"The information wall content cannot be empty"),
    CONTENT_FILE_COUNT_TOO_MANY(12004,"Too many information wall files"),
    CONTENT_FILE_COUNT_EXCEPTION(12005,"Abnormal number of information wall files"),
    CONTENT_FILE_EXCEPTION(12006,"Information wall file abnormality"),
    CONTENT_NOT_YOU(12007,"This is not your message wall"),

    /**
     * 用户
     */
    NOT_LOGGED_IN(11001,"Please log in to operate"),
    EMAIL_NOT_EXIST(11002,"Email does not exist"),

    /**
     * 评论
     */
    COMMENT_IS_NULL(13001,"Comment does not exist"),
    COMMENT_DEL_ERR(13002,"Comment deletion failed"),


    SMS_SEND_CODE_EXCEPTION(10403, "sending failed"),
    PHONE_EXIST_EXCEPTION(15002, "ID number already exists"),
    USER_EXIST_EXCEPTION(15001, "User already exists"),

    PHONE_EXISTS_EXCEPTION(15002, "ID number already exists"),

    LOGIN_ACCOUNT_PASSWORD_INVALID(15003, "Wrong username or password");


    private Integer code;
    private String msg;

    private CampusBizCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
