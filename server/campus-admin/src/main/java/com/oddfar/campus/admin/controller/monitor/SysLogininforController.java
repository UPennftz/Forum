package com.oddfar.campus.admin.controller.monitor;

import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.annotation.Log;
import com.oddfar.campus.common.domain.PageResult;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.domain.entity.SysLoginLogEntity;
import com.oddfar.campus.common.enums.ResBizTypeEnum;
import com.oddfar.campus.framework.service.SysLoginLogService;
import com.oddfar.campus.framework.web.service.SysPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 系统访问记录
 *
 */
@RestController
@RequestMapping("/monitor/logininfor")
@Log(openLog = false)
@ApiResource(name = "Login log management", resBizType = ResBizTypeEnum.SYSTEM)
public class SysLogininforController {
    @Autowired
    private SysLoginLogService logininforService;

    @Autowired
    private SysPasswordService passwordService;

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping(value = "/list",name = "Login Log-Category List")
    public R list(SysLoginLogEntity logininfor) {
        PageResult<SysLoginLogEntity> page = logininforService.selectLogininforPage(logininfor);
        return R.ok().put(page);
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @DeleteMapping(value = "/{infoIds}",name = "Login Log-Delete")
    public R remove(@PathVariable Long[] infoIds) {
        return R.ok(logininforService.deleteLogininforByIds(infoIds));
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @DeleteMapping(value = "/clean",name = "Login Log-Clear")
    public R clean() {
        logininforService.cleanLogininfor();
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:unlock')")
    @GetMapping(value = "/unlock/{userName}",name = "Login Log-Unlock")
    public R unlock(@PathVariable("userName") String userName) {
        passwordService.clearLoginRecordCache(userName);
        return R.ok();
    }
}
