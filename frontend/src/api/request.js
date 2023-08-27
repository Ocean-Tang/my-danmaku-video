// request.js
import axios from 'axios'
import { Message } from 'element-ui'
import userApi from '@/api/user/user'
import Global from '@/components/Global.vue'

// 创建axios实例
const service = axios.create({
  baseURL: '/api', // 基础地址
  timeout: 10000 // 超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么，例如添加token
    let accessToken = localStorage.getItem('accessToken')
    if (accessToken) {
      config.headers['accessToken'] = accessToken
    }
    return config
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 对响应数据做点什么，例如判断状态码
    const res = response.data
    if (res.code === 400005) {
      let config = response.config
      let refreshToken = localStorage.getItem('refreshToken')
      console.log(refreshToken)
      userApi.refresh(refreshToken).then(res => {
        console.log('刷新结果' + res)
        let accessToken = res.data.accessToken
        localStorage.setItem('accessToken', accessToken)
        localStorage.setItem('refreshToken', res.data.refreshToken)
        location.reload()
        // config.headers['accessToken'] = accessToken
        // return axios(config)
      })
    } else if (res.code === 400008) {
      Global.user = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      Message.error(res.msg)
    } else if (res.code === 999999) {
      return res
    } else if (res.code !== 200000) {
      Message.error(res.msg)
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    // 对响应错误做点什么，例如提示错误信息
    return Promise.reject(error)
  }
)

export default service
