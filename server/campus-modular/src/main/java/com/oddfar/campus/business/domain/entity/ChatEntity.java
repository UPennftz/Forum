package com.oddfar.campus.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 客服中心
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2024-07-30 21:14:54
 */
@Data
@TableName("chat")
public class ChatEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId
    private Long id;
    /**
     * 用户id
     */

    private Long userid;

    /**
     * 管理员id
     */

    private Long adminid;

    /**
     * 提问
     */

    private String ask;

    /**
     * 回复
     */

    private String reply;

    /**
     * 是否回复
     */

    private Integer isreply;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addtime;

}
