<template>
  <div class="info" v-if="Global.user">
    <el-form v-if="user" ref="form" :model="user" label-width="80px" :disabled="!isModify">
      <el-form-item label="昵称">
        <el-input v-model="user.nick" placeholder="请输入昵称"></el-input>
      </el-form-item>
      <el-form-item label="头像" >
        <img :src="user.avatar" class="avatar" alt="原头像">
      </el-form-item>
      <el-form-item label="新头像" v-if="isModify">
        <el-upload
          class="avatar-uploader avatar"
          action=""
          :limit="1"
          ref="avatar"
          accept="image/*"
          :auto-upload=false
          :on-change="avatarChange"
          :show-file-list="false">
          <img v-if="imageUrl" :src="imageUrl" class="avatar" alt="新头像">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio v-model="user.gender" label="男" >男</el-radio>
        <el-radio v-model="user.gender" label="女" >女</el-radio>
        <el-radio v-model="user.gender" label="未知" >未知</el-radio>
      </el-form-item>
      <el-form-item label="生日">
        <el-date-picker
          v-model="user.birth"
          value-format="yyyy-MM-dd"
          type="date"
          placeholder="选择日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="签名">
        <el-input
          type="textarea"
          :rows="2"
          placeholder="请输入内容"
          v-model="user.sign">
        </el-input>
      </el-form-item>
      <el-form-item>
        <div v-if="isModify">
          <el-button type="primary" @click="submit">修改</el-button>
          <el-button @click="gotoIndex">取消</el-button>
        </div>
      </el-form-item>
    </el-form>
    <div v-if="!isModify" >
      <el-button @click="gotoIndex">返回</el-button>
    </div>
  </div>
</template>

<script>
import userApi from '@/api/user/user'
import Global from '@/components/Global.vue'

export default {
  name: 'Detail',
  data () {
    return {
      isModify: false,
      user: null,
      imageUrl: null
    }
  },
  mounted () {
    this.getUserInfo()
    this.getIsModify()
  },
  methods: {
    getUserInfo () {
      if (this.$route.query && this.$route.query.id) {
        userApi.getUserInfo(this.$route.query.id).then(res => {
          this.user = res.data
        }).catch(() => {
          this.$message.error('查看资料失败')
        })
        return
      }
      if (Global.user) {
        this.user = JSON.parse(JSON.stringify(Global.user))
        return
      }
      this.$message.error('请登录！')
      this.$router.push('/login')
    },
    getIsModify () {
      console.log(this.$route.query.isModify)
      this.isModify = this.$route.query.isModify
    },
    avatarChange () {
      let file = this.$refs.avatar.uploadFiles[0].raw
      this.imageUrl = URL.createObjectURL(file)
      this.user.avatarFile = file
    },
    gotoIndex () {
      this.$router.push('/')
    },
    submit () {
      userApi.updateInfo(this.user).then(() => {
        this.$router.push('/')
        this.$message.success('修改成功')
        location.reload()
      })
    }
  },
  watch: {
    $route (to, from) {
      if (to.name === from.name && to.query !== from.query) {
        console.log(to.query, from.query)
        this.isModify = to.query.isModify
      }
    }
  }
}
</script>

<style scoped>
.avatar-uploader .el-upload {
  border: 3px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.info {
  width: 50%
}

.el-form-item {
  text-align: left;
}
</style>
