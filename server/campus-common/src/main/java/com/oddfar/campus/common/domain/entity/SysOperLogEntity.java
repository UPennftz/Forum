package com.oddfar.campus.common.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志实体类 sys_log
 */
@Data
@TableName("sys_log_oper")
public class SysOperLogEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 日志主键
     */
    @TableId("oper_id")
    private Long operId;

    /**
     * 服务名称，一般为spring.application.name
     */
    private String appName;

    /**
     * 操作模块
     */
    private String logName;

    /**
     * 日志记录内容
     */
    private String logContent;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作人员 id
     */
    private Long operUserId;

    /**
     * 请求url
     */
    private String operUrl;

    /**
     * 操作地址
     */
    private String operIp;


    /**
     * 请求参数
     */
    private String operParam;

    /**
     * 返回参数
     */
    private String jsonResult;


    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+1")
    private Date operTime;


    private static final Integer PAGE_NUM = 1;
    private static final Integer PAGE_SIZE = 10;

    @TableField(exist = false)
    private Map<String, Object> params;

    @NotNull(message = "Page number cannot be empty")
    @Min(value = 1, message = "The minimum page number is 1")
    @TableField(exist = false)
    @JsonIgnore
    private Integer pageNum = PAGE_NUM;

    @NotNull(message = "The number of entries per page cannot be empty")
    @Min(value = 1, message = "The minimum number of entries per page is 1")
    @Max(value = 100, message = "The maximum number of entries per page is 100")
    @TableField(exist = false)
    @JsonIgnore
    private Integer pageSize = PAGE_SIZE;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }
}
