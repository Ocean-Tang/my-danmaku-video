<template>
  <div id="app">
    <el-container v-if="isGetInfo">
      <el-header id="header">
        <el-row v-if="user == null">
          <el-col :span="18" >
            <div class="logo" @click="index()">
              弹幕视频网站
            </div>
          </el-col>
          <el-col :span="6">
            <router-link to="/login">
              <el-button type="primary">登录</el-button>
            </router-link>
            <router-link to="/signup">
              <el-button type="primary">注册</el-button>
            </router-link>
          </el-col>
        </el-row>
        <el-row v-if="user != null">
          <el-col :span="16" >
            <div class="logo" @click="index()">
              弹幕视频网站
            </div>
          </el-col>
          <el-col :span="2">
            <el-dropdown>
              <span class="el-dropdown-link">
                <el-avatar size="medium" :src="user.avatar"/>
                {{user.nick}}
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item ><div @click="detail()">查看资料</div></el-dropdown-item>
                <el-dropdown-item ><div @click="modify()">修改资料</div></el-dropdown-item>
                <el-dropdown-item ><div @click="history()">观看记录</div></el-dropdown-item>
                <el-dropdown-item ><div @click="collection()">收藏列表</div></el-dropdown-item>
                <el-dropdown-item ><div @click="getSubscriptions">关注用户</div></el-dropdown-item>
                <el-dropdown-item ><div @click="subscribed()">粉丝列表</div></el-dropdown-item>
                <el-dropdown-item ><div @click="works()">作品列表</div></el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-col>
          <el-col :span="6">
            <el-dropdown trigger="click" @command="play">
              <el-badge :value="msgList.length" class="item">
                <i class="el-icon-message-solid msg" />
              </el-badge>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-for="(msg, index) in msgList" :command="index" :key="msg.id">
                  你订阅的{{msg.nick}} 发布了新的视频 <span class="msg-title">《{{msg.title}}》</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <router-link to="/upload-video">
              <el-button type="primary" >上传视频</el-button>
            </router-link>
            <el-button type="danger" @click="logout()">退出</el-button>
          </el-col>
        </el-row>
      </el-header>
      <el-main id="component">
        <router-view/>
      </el-main>
    </el-container>

  </div>
</template>

<script>
import Global from '@/components/Global.vue'
import userApi from '@/api/user/user'

export default {
  name: 'App',
  data () {
    return {
      isGetInfo: false, // 解决用户点击刷新按钮刷新页面后，异步请求导致部分组件显示异常问题
      user: null,
      msgList: []
    }
  },
  mounted () {
    this.getInfo()
  },
  methods: {
    getInfo () {
      if (localStorage.getItem('accessToken') === null) {
        this.isGetInfo = true
        Global.user = null
        return
      }
      userApi.getInfo().then((res) => {
        if (res.code === 200000) {
          this.Global.user = res.data
          this.user = res.data
          this.getMsg()
          this.isGetInfo = true
        }
      }).catch(() => {
        this.isGetInfo = true
        Global.user = null
        this.user = null
      })
    },
    logout () {
      // 弹框确认
      this.$confirm('确认退出登录吗？', '退出登录', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.user = null
        userApi.logout().then().catch().finally(() => {
          Global.user = null
          localStorage.removeItem('accessToken')
          localStorage.removeItem('refreshToken')
        })
        this.$message({
          type: 'success',
          message: '退出成功!'
        })
      }).catch()
    },
    getMsg () {
      userApi.getMsg().then(res => {
        this.msgList = res.data
      })
    },
    play (index) {
      userApi.consumeMsg(index).then(() => {
        this.getMsg()
        this.$router.push({
          path: '/video-player',
          query: {
            videoId: this.msgList[index].id
          }
        })
      })
    },
    index () {
      this.$router.push('/')
    },
    detail () {
      this.$router.push({
        name: 'userDetail',
        query: {
          isModify: false
        }
      })
    },
    modify () {
      this.$router.push({
        name: 'userDetail',
        query: {
          isModify: true
        }
      })
    },
    history () {
      this.$router.push('/user/history')
    },
    collection () {
      this.$router.push('/user/collection')
    },
    works () {
      this.$router.push({
        path: '/user/works',
        query: {
          id: this.user.id
        }
      })
    },
    getSubscriptions () {
      this.$router.push('/user/subscription')
    },
    subscribed () {
      this.$router.push('/user/subscribed')
    }
  }
}
</script>

<style>
#header {
  margin-top: 60px;
  margin-left: 50px;
  margin-right: 50px;
}

.logo {
  font-size: 36px;
  font-weight: bold;
  color: #0c0909;
}

#component {
  color: #2c3e50;
  margin: 0 60px;
}

.msg {
  margin: 5px 5px;
  font-size: 18px;
}
.item {
  margin-top: 10px;
  margin-right: 10px;
}

.msg-title {
  color: cornflowerblue;
  font-style: italic;
}
</style>
