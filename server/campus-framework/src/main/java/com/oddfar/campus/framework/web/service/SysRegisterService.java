package com.oddfar.campus.framework.web.service;

import com.oddfar.campus.common.constant.CacheConstants;
import com.oddfar.campus.common.constant.Constants;
import com.oddfar.campus.common.constant.UserConstants;
import com.oddfar.campus.common.core.RedisCache;
import com.oddfar.campus.common.domain.entity.SysUserEntity;
import com.oddfar.campus.common.domain.model.RegisterBody;
import com.oddfar.campus.common.exception.user.CaptchaException;
import com.oddfar.campus.common.exception.user.CaptchaExpireException;
import com.oddfar.campus.common.utils.MessageUtils;
import com.oddfar.campus.common.utils.SecurityUtils;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.manager.AsyncFactory;
import com.oddfar.campus.framework.manager.AsyncManager;
import com.oddfar.campus.framework.service.SysConfigService;
import com.oddfar.campus.framework.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册校验方法
 */
@Component
public class SysRegisterService {
    @Autowired
    private SysUserService userService;

    @Autowired
    private SysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody) {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        SysUserEntity sysUser = new SysUserEntity();
        sysUser.setUserName(username);

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username)) {
            msg = "Username cannot be empty";
        } else if (StringUtils.isEmpty(password)) {
            msg = "User password cannot be empty";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "The account must be between 2 and 20 characters long.";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "Password must be between 5 and 20 characters long";
        } else if (!(userService.checkUserNameUnique(sysUser))) {
            msg = "Failed to save user'" + username + "', the registered account already exists";
        } else {
            sysUser.setNickName(username);
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag) {
                msg = "Registration failed, please contact the system administrator";
            } else {
                //注册失败异步记录信息
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, null, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException();
        }
    }
}
