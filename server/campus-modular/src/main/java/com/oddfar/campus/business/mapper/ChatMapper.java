package com.oddfar.campus.business.mapper;

import com.oddfar.campus.business.domain.entity.ChatEntity;
import com.oddfar.campus.business.domain.vo.ChatVo;
import com.oddfar.campus.common.core.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 */
@Mapper
public interface ChatMapper extends BaseMapperX<ChatEntity> {



    List<ChatVo> queryChatList(ChatVo chatVo);
}




