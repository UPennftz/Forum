package com.oddfar.campus.business.controller.web;

//import com.oddfar.campus.business.core.api.MailServiceApi;
import com.oddfar.campus.business.enums.CampusBizCodeEnum;
import com.oddfar.campus.business.system.UserPwdResetService;
import com.oddfar.campus.common.annotation.Anonymous;
import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.domain.entity.SysUserEntity;
import com.oddfar.campus.common.domain.model.LoginUser;
import com.oddfar.campus.common.domain.model.RegisterBody;
import com.oddfar.campus.common.enums.ResBizTypeEnum;
import com.oddfar.campus.common.utils.SecurityUtils;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户账户操作api
 * 绑定邮箱等
 *
 * 
 */
@RestController
@RequestMapping("/campus")
@ApiResource(name = "User account operation api", appCode = "campus", resBizType = ResBizTypeEnum.BUSINESS)
public class UserAccountController {


    @Autowired
    private SysUserService userService;
    @Autowired
    private UserPwdResetService pwdResetService;

    /**
     * 判断有没有绑定邮箱
     *
     * @return
     */
    @GetMapping("/haveMail")
    public R haveMail() {
        SysUserEntity user = SecurityUtils.getLoginUser().getUser();
        if (StringUtils.isEmpty(user.getEmail())) {
            return R.error("Email is not bound, please bind your email first");
        } else {
            return R.ok();
        }
    }

    /**
     * 绑定/更换邮件
     *
     * @return
     */
    @PostMapping("/bindMail")
    public R sendMail(@RequestBody RegisterBody registerBody) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(registerBody.getPassword(), password)) {
            return R.error("Wrong password");
        } else {
//            mailServiceApi.bindMail(registerBody);
            return R.ok("Please check");
        }

    }



    /**
     * 给邮箱发送修改密码的验证码
     */
    @PostMapping("/pwd-code")
    @Anonymous
    public R sendResetPwdCode(@RequestBody RegisterBody body) {
        SysUserEntity user = new SysUserEntity();
        user.setEmail(body.getEmail());

        //true为唯一，代表不存在邮箱
        if (StringUtils.isNotEmpty(body.getEmail()) && userService.checkEmailUnique(user)) {
            return R.error(CampusBizCodeEnum.EMAIL_NOT_EXIST.getCode(), CampusBizCodeEnum.EMAIL_NOT_EXIST.getMsg());
        }
        pwdResetService.sendResetPwdCode(body.getEmail(), body.getCode(), body.getUuid());

        return R.ok();
    }

    /**
     * 根据code修改密码
     */
    @PostMapping("/changePwd")
    @Anonymous
    public R changePwd(@RequestBody RegisterBody body) {
        pwdResetService.resetPwd(body.getEmail(), body.getCode(), body.getPassword());
        return R.ok();
    }


}
