package com.oddfar.campus.admin.controller.system;

import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.domain.entity.SysUserEntity;
import com.oddfar.campus.common.domain.model.LoginUser;
import com.oddfar.campus.common.enums.ResBizTypeEnum;
import com.oddfar.campus.common.utils.SecurityUtils;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.api.file.FileUploadUtils;
import com.oddfar.campus.framework.api.file.MimeTypeUtils;
import com.oddfar.campus.framework.api.sysconfig.ConfigExpander;
import com.oddfar.campus.framework.service.SysUserService;
import com.oddfar.campus.framework.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.oddfar.campus.common.utils.SecurityUtils.getLoginUser;

/**
 * 个人信息 业务处理
 */
@RestController
@RequestMapping("/system/user/profile")
@ApiResource(name = "Personal Information Management", resBizType = ResBizTypeEnum.SYSTEM)
public class SysProfileController {
    @Autowired
    private SysUserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 个人信息
     */
    @GetMapping(name = "Personal Information Management-Inquiry")
    public R profile() {
        LoginUser loginUser = getLoginUser();
        SysUserEntity user = loginUser.getUser();
        R ajax = R.ok(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        return ajax;
    }

    /**
     * 修改用户
     */
    @PutMapping(value = "/update", name = "Personal Information Management-Reset Password")
    public R updateProfile(@RequestBody SysUserEntity user) {
        LoginUser loginUser = getLoginUser();
        SysUserEntity sysUser = loginUser.getUser();
        user.setUserName(sysUser.getUserName());
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && !(userService.checkPhoneUnique(user))) {
            return R.error("Modify User'" + user.getUserName() + "'failed, ID number already exists");
        }
        if (StringUtils.isNotEmpty(user.getEmail())
                && !(userService.checkEmailUnique(user))) {
            return R.error("Modify User'" + user.getUserName() + "'failed, email already exists");
        }
        user.setUserId(sysUser.getUserId());
        user.setPassword(null);
        user.setAvatar(null);
        if (userService.updateUserProfile(user) > 0) {
            // 更新缓存用户信息
            sysUser.setNickName(user.getNickName());
            sysUser.setPhonenumber(user.getPhonenumber());
            sysUser.setEmail(user.getEmail());
            sysUser.setSex(user.getSex());
            tokenService.setLoginUser(loginUser);
            return R.ok();
        }
        return R.error("There was an abnormality when modifying personal information. Please contact the administrator");
    }

    /**
     * 重置密码
     */
    @PutMapping(value = "/updatePwd", name = "Personal Information Management-Reset Password")
    public R updatePwd(String oldPassword, String newPassword) {
        LoginUser loginUser = getLoginUser();
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            return R.error("Failed to change password, old password is wrong");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return R.error("The new password cannot be the same as the old password");
        }
        if (userService.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)) > 0) {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            tokenService.setLoginUser(loginUser);
            return R.ok();
        }
        return R.error("Password modification is abnormal, please contact the administrator");
    }

    /**
     * 头像上传
     */
    @PostMapping(value = "/avatar", name = "Personal Information Management-Avatar Last")
    public R avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            LoginUser loginUser = getLoginUser();
            String avatar = FileUploadUtils.upload(ConfigExpander.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            if (userService.updateUserAvatar(loginUser.getUsername(), avatar)) {
                R ajax = R.ok();
                ajax.put("imgUrl", avatar);
                // 更新缓存用户头像
                loginUser.getUser().setAvatar(avatar);
                tokenService.setLoginUser(loginUser);
                return ajax;
            }
        }
        return R.error("The uploaded image is abnormal, please contact the administrator");
    }
}
