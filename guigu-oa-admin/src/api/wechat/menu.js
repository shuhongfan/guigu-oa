import request from '@/utils/request'

const api_name = '/admin/wechat/menu'

export default {

  findMenuInfo() {
    return request({
      url: `${api_name}/findMenuInfo`,
      method: `get`
    })
  },

  save(menu) {
    return request({
      url: `${api_name}/save`,
      method: `post`,
      data: menu
    })
  },

  getById(id) {
    return request({
      url: `${api_name}/get/${id}`,
      method: `get`
    })
  },

  updateById(menu) {
    return request({
      url: `${api_name}/update`,
      method: `put`,
      data: menu
    })
  },

  removeById(id) {
    return request({
      url: `${api_name}/remove/${id}`,
      method: 'delete'
    })
  },
  syncMenu() {
    return request({
      url: `${api_name}/syncMenu`,
      method: `get`
    })
  },
  removeMenu() {
    return request({
      url: `${api_name}/removeMenu`,
      method: `delete`
    })
  }
}
