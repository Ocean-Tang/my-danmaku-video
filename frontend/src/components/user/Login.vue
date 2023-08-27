<template>
  <div>
    <el-form ref="form" :model="user" label-width="80px">
      <el-form-item label="邮箱">
        <el-input v-model="user.email"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input type="password" v-model="user.password"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="login">登录</el-button>
        <router-link to="/"><el-button>取消</el-button></router-link>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import userApi from '@/api/user/user'

export default {
  name: 'Login',
  data () {
    return {
      user: {}
    }
  },
  methods: {
    login () {
      userApi.login(this.user).then(res => {
        userApi.loginSuccess(this, res)
      })
    }
  }
}
</script>

<style scoped>

</style>
