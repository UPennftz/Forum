package com.oddfar.campus.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oddfar.campus.common.domain.BaseEntity;
import com.oddfar.campus.common.validator.Xss;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * 用户对象 sys_user
 */

@TableName("sys_user")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SysUserEntity extends BaseEntity  {
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId("user_id")
    private Long userId;

    /** 用户账号 */
    @Xss(message = "User accounts cannot contain script characters")
    @NotBlank(message = "User account cannot be empty")
    @Size(min = 0, max = 30, message = "User account length cannot exceed 30 characters")
    private String userName;

    /** 用户昵称 */
    @Xss(message = "Usernames cannot contain script characters")
    @Size(min = 0, max = 30, message = "Username length cannot exceed 30 characters")
    private String nickName;

    /** 用户邮箱 */
    @Email(message = "The email format is incorrect")
    @Size(min = 0, max = 50, message = "Email address cannot exceed 50 characters")
    private String email;

    /** 手机号码 */
    @Size(min = 0, max = 11, message = "The ID number cannot exceed 11 characters.")
    private String phonenumber;

    /** 用户性别 */
    private String sex;

    /** 用户头像 */
    private String avatar;

    /** 密码 */
//    @JsonIgnore
    private String password;

    /** 帐号状态（0正常 1停用） */
    private String status;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    private Date loginDate;

    /** 备注 */
    private String remark;

    /** 角色对象 */
    @TableField(exist = false)
    private List<SysRoleEntity> roles;

    /** 资源对象 */
//    @TableField(exist = false)
//    private List<SysResourceEntity> resources;

    /** 角色组 */
    @TableField(exist = false)
    private Long[] roleIds;

    /** 岗位组 */
    @TableField(exist = false)
    private Long[] postIds;

    /** 角色ID */
    @TableField(exist = false)
    private Long roleId;

    public SysUserEntity(Long userId){
        this.userId = userId;
    }

    public boolean isAdmin()
    {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

}