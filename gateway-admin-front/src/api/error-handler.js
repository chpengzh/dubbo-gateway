import {Notification} from 'element-ui'

export function errorHandle(err) {
  if (err.response && err.response.data) {
    let resp = err.response.data
    if (resp && resp.code) {
      Notification.error({
        title: '请求错误(' + resp.code + ')',
        message: resp.msg,
        type: 'error'
      })
      return
    }
  }
  console.log(err)
  Notification.error({
    title: '服务端错误',
    message: err,
    type: 'error'
  })
}
