package com.oddfar.campus.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oddfar.campus.business.domain.entity.ChatEntity;
import com.oddfar.campus.business.domain.vo.ChatVo;

import java.util.List;

public interface ChatService extends IService<ChatEntity> {


    List<ChatVo> queryChatList(ChatVo vo);


}
