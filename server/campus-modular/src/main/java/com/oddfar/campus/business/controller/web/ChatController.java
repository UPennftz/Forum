package com.oddfar.campus.business.controller.web;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.oddfar.campus.business.domain.entity.ChatEntity;
import com.oddfar.campus.business.domain.vo.ChatVo;
import com.oddfar.campus.business.service.ChatService;
import com.oddfar.campus.common.annotation.ApiResource;
import com.oddfar.campus.common.core.LambdaQueryWrapperX;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.domain.entity.SysUserEntity;
import com.oddfar.campus.common.domain.model.LoginUser;
import com.oddfar.campus.common.enums.ResBizTypeEnum;
import com.oddfar.campus.common.utils.SecurityUtils;
import com.oddfar.campus.framework.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 客服中心Controller
 */
@RestController
@RequestMapping("/chat")
@ApiResource(name = "Customer Service Center Management", appCode = "chat", resBizType = ResBizTypeEnum.BUSINESS)
public class ChatController {
    @Autowired
    private ChatService ChatService;
    @Autowired
    private SysUserService userService;

    /**
     * 查询客服中心列表：当前登陆人
     */
    @GetMapping("/list")
    public R list() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<ChatEntity> list = ChatService.list(new LambdaQueryWrapperX<ChatEntity>()
                .eq(ChatEntity::getUserid, String.valueOf(loginUser.getUserId())));
        return R.ok().put(list);
    }

    /**
     * 查询客服中心列表：管理员查询，只拿未回复的数据
     */
    @PostMapping("/listByAdmin")
    public R listByAdmin(@RequestBody ChatVo chat) {
        List<ChatVo> list = ChatService.queryChatList(chat);
        return R.ok().put(list);
    }

    /**
     * 查询客服中心列表：通过人员id
     */
    @PostMapping("/listByUser")
    public R listByUser(@Validated @RequestBody ChatEntity chat) {
        List<ChatEntity> list = ChatService.list(new LambdaQueryWrapperX<ChatEntity>()
                .eq(ChatEntity::getUserid, String.valueOf(chat.getUserid())));
        return R.ok().put(list);
    }

    /**
     * 新增信息
     */
    @PostMapping(value = "/add", name = "New information")
    public R add(@Validated @RequestBody ChatEntity chat) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //将所有的信息置为已回复
        UpdateWrapper<ChatEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("isreply", "1")
                .eq("userid", loginUser.getUserId());
        ChatService.update(updateWrapper);
        //新增一条未回复的数据
        chat.setAddtime(new Date());
        chat.setAsk(chat.getAsk());
        chat.setUserid(loginUser.getUserId());
        chat.setId(IdWorker.getId());
        chat.setIsreply(0);
        return R.ok(ChatService.save(chat));
    }
    /**
     * 新增回复信息
     */
    @PostMapping(value = "/reply", name = "Add reply information")
    public void reply(@Validated @RequestBody ChatEntity chat) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //新增一条未回复的数据
        chat.setAddtime(new Date());
        chat.setReply(chat.getAsk());
        chat.setAsk("");
        chat.setAdminid(loginUser.getUserId());
        chat.setId(IdWorker.getId());
        chat.setIsreply(1);
        R.ok(ChatService.save(chat));

        //将所有的信息置为已回复
        UpdateWrapper<ChatEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("isreply", "1")
                .eq("userid", chat.getUserid());
        ChatService.update(updateWrapper);

    }
//
//    /**
//     * 修改客服中心
//     */
//    @PostMapping(value = "/add", name = "修改信息")
//    public R edit(@Validated @RequestBody ChatEntity Chat) {
//        return R.ok(ChatService.updateById(Chat));
//    }
//
//    /**
//     * 删除客服中心
//     */
//
//    @DeleteMapping("/{ChatIds}")
//    public R remove(@PathVariable Long[] ChatIds) {
//        return R.ok(ChatService.removeBatchByIds((Arrays.asList(ChatIds))));
//    }
}
