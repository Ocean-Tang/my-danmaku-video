<template>
  <div>
    <el-form ref="form" :model="user" label-width="80px">
      <el-form-item label="昵称" prop="nick" :rules="[
        { required: true, message: '请输入昵称', trigger: 'blur' },
        { min: 2, max: 10, message: '长度在 2 到 10 之间', trigger: 'blur' }
        ]">
        <el-input v-model="user.nick"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email"
                    :rules="[
                      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
                      { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
                      ]">
        <el-input v-model="user.email"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password"
                    :rules="[{ required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, max: 16, message: '长度在 6 到 16 之间', trigger: 'blur' }]">
        <el-input type="password" v-model="user.password"></el-input>
      </el-form-item>
      <el-form-item label="验证码" prop="captcha"
                    :rules="[{required: true, message: '请输入验证码', trigger: 'blur'},
                    { min: 6, max: 6, message: '请输入正确的验证码', trigger: 'blur' }]">
        <el-input v-model="user.captcha" ></el-input>
        <el-button @click="getCaptcha" :disabled="disableGetCaptcha"> {{getCaptchaMessage}} </el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="signup">注册</el-button>
        <router-link to="/"><el-button>取消</el-button></router-link>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import userApi from '@/api/user/user'

export default {
  name: 'Signup',
  data () {
    return {
      user: {},
      disableGetCaptcha: false,
      getCaptchaMessage: '获取验证码'
    }
  },
  methods: {
    signup () {
      this.$refs.form.validate(valid => {
        if (valid) {
          const signInfo = JSON.parse(JSON.stringify(this.user))
          const loginInfo = JSON.parse(JSON.stringify(this.user))
          userApi.signup(signInfo).then(res => {
            userApi.login(loginInfo).then(res => {
              userApi.loginSuccess(this, res)
            })
          })
        } else {
          this.$message.error('请输入完整信息！')
        }
      })
    },
    getCaptcha () {
      /* 定时器，禁止按钮 */
      let time = 60
      let timer = setInterval(() => {
        time--
        if (time >= 0) {
          this.getCaptchaMessage = '获取验证码（' + time + 's)'
          this.disableGetCaptcha = true
        } else {
          this.getCaptchaMessage = '获取验证码'
          this.disableGetCaptcha = false
          clearInterval(timer)
        }
      }, 1000)
      /* 定时器，禁止按钮 */
      // 获取验证码
      userApi.getCaptcha(this.user.email).then(res => {}).catch(() => {
        this.getCaptchaMessage = '获取验证码'
        this.disableGetCaptcha = false
        clearInterval(timer)
      })
    }
  }
}
</script>

<style scoped>

</style>
