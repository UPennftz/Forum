package com.oddfar.campus.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 系统访问记录表 sys_logininfor
 *
 */
@Data
@TableName("sys_log_login")
public class SysLoginLogEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId("info_id")
    private Long infoId;

    /**
     * 登录成功的用户id
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 登录状态 0成功 1失败
     */
    private String status;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+1")
    private Date loginTime;


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
