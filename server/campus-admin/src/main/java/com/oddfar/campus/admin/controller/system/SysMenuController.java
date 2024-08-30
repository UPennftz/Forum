package com.oddfar.campus.admin.controller.system;

import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.constant.UserConstants;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.domain.entity.SysMenuEntity;
import com.oddfar.campus.common.enums.ResBizTypeEnum;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.oddfar.campus.common.utils.SecurityUtils.getUserId;

@RestController
@RequestMapping("/system/menu")
@ApiResource(name = "Menu Management", resBizType = ResBizTypeEnum.SYSTEM)
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping(value = "/list", name = "Menu Management-Pagination")
    public R list(SysMenuEntity menu) {
        List<SysMenuEntity> menus = menuService.selectMenuList(menu, getUserId());
        return R.ok(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}", name = "Menu Management-Query")
    public R getInfo(@PathVariable Long menuId) {
        return R.ok(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping(value = "/treeselect", name = "Menu Management-Get Menu Drop-Down Tree List")
    public R treeSelect(SysMenuEntity menu) {
        List<SysMenuEntity> menus = menuService.selectMenuList(menu, getUserId());
        return R.ok(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}", name = "Menu Management-Load the corresponding character menu list tree")
    public R roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        List<SysMenuEntity> menus = menuService.selectMenuList(getUserId());
        R ajax = R.ok();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @PostMapping(name = "Menu Management-New")
    public R add(@Validated @RequestBody SysMenuEntity menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.error("Failed to add menu'" + menu.getMenuName() + "', the menu name already exists");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return R.error("Adding menu'" + menu.getMenuName() + "'failed, the address must start with http(s)://");
        }
        return R.ok(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @PutMapping(name = "Menu Management-Modify")
    public R edit(@Validated @RequestBody SysMenuEntity menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.error("Failed to modify menu'" + menu.getMenuName() + "', the menu name already exists");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return R.error("Failed to modify menu'" + menu.getMenuName() + "'the address must start with http(s)://");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return R.error("Failed to modify menu'" + menu.getMenuName() + "'the parent menu cannot select itself.");
        }
        return R.ok(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @DeleteMapping(value = "/{menuId}", name = "Menu Management-Delete")
    public R remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return R.error("There is a submenu, deletion is not allowed");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return R.error("The menu has been assigned, deletion is not allowed");
        }
        return R.ok(menuService.deleteMenuById(menuId));
    }





}
