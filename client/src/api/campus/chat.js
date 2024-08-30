import request from '@/utils/request'

// 查询客服投诉列表
export function listByAdmin(data) {
  return request({
    url: '/chat/listByAdmin',
    method: 'post',
    data: data
  })
}
export function listByUser(chat) {
  return request({
    url: '/chat/listByUser',
    method: 'post',
    data: chat
  })
}

// 新增客服回复
export function reply(data) {
  return request({
    url: '/chat/reply',
    method: 'post',
    data: data
  })
}



// 查询客服投诉详细
export function getchat(chatId) {
  return request({
    url: '/chat/' + chatId,
    method: 'get'
  })
}

// 新增客服投诉
export function addchat(data) {
  return request({
    url: '/chat',
    method: 'post',
    data: data
  })
}

// 修改客服投诉
export function updatechat(data) {
  return request({
    url: '/chat',
    method: 'put',
    data: data
  })
}

// 删除客服投诉
export function delchat(chatId) {
  return request({
    url: '/chat/' + chatId,
    method: 'delete'
  })
}
