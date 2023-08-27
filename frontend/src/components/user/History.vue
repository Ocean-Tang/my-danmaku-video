<template>
  <div class="historyList">
    <el-card class="history" :key="history.id" v-for="history in historyList">
      <el-container>
        <el-aside width="45%" @click="play(history.id)">
          <div class="cover-box">
            <img class="cover" :src="history.cover"/>
          </div>
        </el-aside>
        <el-container>
          <el-header><el-link @click="play(history.id)" type="primary" >{{history.title}}</el-link></el-header>
          <el-footer>
            <div>
              <UserPopUp :avatar="history.avatar" :id="history.userId" :nick="history.nick" />
              <span class="separator"> | </span>
              <span class="viewTime"> {{history.viewTime}} </span>
              <span class="separator"> | </span>
              <span class="progress"> {{history.progress}} %</span>
            </div>
          </el-footer>
        </el-container>
      </el-container>
    </el-card>
  </div>
</template>

<script>
import Global from '@/components/Global.vue'
import videoApi from '@/api/video/video'
import UserPopUp from '@/components/user/UserPopUp.vue'

export default {
  name: 'History',
  components: {
    UserPopUp
  },
  data () {
    return {
      historyList: []
    }
  },
  mounted () {
    this.getHistoryList()
  },
  methods: {
    getHistoryList () {
      if (!Global.user) {
        this.$message.error('请登录')
        this.$router.push('/login')
        return
      }
      videoApi.getHistoryList().then(res => {
        this.historyList = res.data
      })
    },
    play (videoId) {
      this.$router.push({
        name: 'videoPlayer',
        query: {
          videoId: videoId
        }
      })
    }
  }
}
</script>

<style scoped>
.historyList {
  margin-left: 25%;
  margin-right: 15%;
  width: 50%;
}

.history {
  margin: 20px 0;
}

.cover-box {
  width: 100%;
  height: 110px;
}

.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.viewTime {
  color: #999999;
  font-size: 13px;
}

.separator {
  margin: 0 5px;
}

.progress {
  font-size: 13px;
}
</style>
