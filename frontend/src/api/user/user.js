// apis.js
import request from '../request'
import JSEncrypt from 'jsencrypt'

const api = {
  getCaptcha (email) {
    return request({
      url: `user/mail-captcha?email=${email}`,
      method: 'post'
    })
  },
  loginSuccess (app, res) {
    app.$message.success(res.msg)
    localStorage.setItem('accessToken', res.data.accessToken)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    app.$router.push('/')
    location.reload()
  },
  login (user) {
    return request({
      url: '/user/login',
      method: 'post',
      data: {
        'email': encode(user.email),
        'password': encode(user.password)
      }
    })
  },
  refresh (refreshToken) {
    return request({
      url: `/user/refresh-token?refreshToken=${refreshToken}`,
      method: 'put'
    })
  },
  signup (user) {
    user.email = encode(user.email)
    user.password = encode(user.password)
    return request({
      url: '/user/signup',
      method: 'post',
      data: user
    })
  },
  getInfo () {
    return request({
      url: '/user/info',
      method: 'get'
    })
  },
  logout () {
    return request({
      url: '/user/logout',
      method: 'delete'
    })
  },
  getMsg () {
    return request({
      url: '/user/subscribe/msg',
      method: 'get'
    })
  },
  consumeMsg (index) {
    return request({
      url: `/user/subscribe/msg/${index}`,
      method: 'delete'
    })
  },
  subscribe (userId) {
    return request({
      url: `/user/subscribe/${userId}`,
      method: 'post'
    })
  },
  cancelSubscribe (userId) {
    return request({
      url: `/user/subscribe/${userId}`,
      method: 'delete'
    })
  },
  getIsSubscribe (userId) {
    return request({
      url: `/user/is-subscription/${userId}`,
      method: 'get'
    })
  },
  updateInfo (user) {
    let formData = new FormData()
    if (user.avatarFile) {
      formData.append('avatarFile', user.avatarFile)
    }
    formData.append('id', user.id)
    formData.append('nick', user.nick)
    formData.append('gender', user.gender)
    formData.append('sign', user.sign)
    formData.append('birth', user.birth)
    return request({
      url: `/user/info`,
      method: 'put',
      headers: {
        'Content-Type': 'multipart/form-data' // 设置请求头为multipart/form-data
      },
      data: formData
    })
  },
  getUserInfo (id) {
    return request({
      url: `/user/info/${id}`,
      method: 'get'
    })
  },
  getSubscriptions () {
    return request({
      url: '/user/subscription',
      method: 'get'
    })
  },
  getSubscribedList () {
    return request({
      url: '/user/subscribed',
      method: 'get'
    })
  }
}

const encryptor = new JSEncrypt()
const publicKey = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6OiBa3fwgTRS0V2pzC6x6b9j1SzAvt+koLVsk758tM5NqwIdkKodSrou2BJiDaWCpkoepm8IAOnqAV+EUNSGr+AiO8kyaZKVOA1gWLw/aJlNojgNbIaS/geU0iELvVBh3ZuoCgdWX6Nxo3CXPqqdYbcl4O+mtafLwqNcNr0l95wIDAQAB'
encryptor.setPublicKey(publicKey)
function encode (content) {
  return encryptor.encrypt(content)
}

export default api
