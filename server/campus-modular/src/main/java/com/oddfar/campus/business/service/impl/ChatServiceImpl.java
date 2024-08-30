package com.oddfar.campus.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oddfar.campus.business.domain.entity.ChatEntity;
import com.oddfar.campus.business.domain.vo.ChatVo;
import com.oddfar.campus.business.mapper.ChatMapper;
import com.oddfar.campus.business.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, ChatEntity>
        implements ChatService {

    @Autowired
    private ChatMapper ChatMapper;


    @Override
    public List<ChatVo> queryChatList(ChatVo chatVo) {
        return ChatMapper.queryChatList(chatVo);
    }
}
