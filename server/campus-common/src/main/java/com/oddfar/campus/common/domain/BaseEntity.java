package com.oddfar.campus.common.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity基类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Integer PAGE_NUM = 1;
    private static final Integer PAGE_SIZE = 10;


    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+1")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+1")
    private Date updateTime;

    @TableField(fill = FieldFill.UPDATE)
    private Long updateUser;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    private Integer delFlag;

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
