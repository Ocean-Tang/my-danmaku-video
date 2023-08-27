<template>
  <div>
    <div v-if="!isPublish">
      当前视频：{{videoInfo.status}}
    </div>
    <div v-if="isPublish">
      <div class="player-div" >
        <el-card  class="player-card" shadow="hover" >
          <div class="user-info">
            <h3 class="video-title">{{ videoInfo.title }}</h3>
            <div class="info-container">
              <UserPopUp v-if="videoInfo.userId" :avatar="videoInfo.avatar" :id="videoInfo.userId" :nick="videoInfo.nick" />
            </div>
          </div>
          <d-player ref="player" id="player" :options="options"></d-player>
          <div class="icon-container">
            <el-tooltip effect="dark" content="点赞" placement="bottom">
              <div class="icon-item">
                <img class="like-icon" src="@/assets/like.png" v-if="!like" alt="点赞" @click="likeVideo"/>
                <img class="like-icon" src="@/assets/like-click.png" v-if="like" alt="点赞" @click="likeVideo"/>
                <span class="count">{{videoInfo.like}}</span>
              </div>
            </el-tooltip>
            <el-tooltip effect="dark" content="收藏" placement="bottom">
              <div class="icon-item">
                <img class="like-icon" src="@/assets/collect.png" v-if="!collect" alt="收藏" @click="collectVideo">
                <img class="like-icon" src="@/assets/collect-click.png" v-if="collect" alt="收藏" @click="collectVideo">
                <span class="count">{{videoInfo.collect}}</span>
              </div>
            </el-tooltip>
            <el-tooltip effect="dark" content="观看次数" placement="bottom">
              <div class="icon-item">
                <img class="like-icon" src="@/assets/view.png" alt="观看次数" >
                <span class="count">{{videoInfo.count}}</span>
              </div>
            </el-tooltip>
            <el-tooltip effect="dark" content="当前同时在看人数" placement="bottom">
              <div class="icon-item">
                <img class="like-icon" src="@/assets/viewers.png" alt="当前同时在看人数" >
                <span class="count">{{viewers}}</span>
              </div>
            </el-tooltip>
            <el-tooltip effect="dark" content="弹幕数量" placement="bottom">
              <div class="icon-item">
                <img class="like-icon" src="@/assets/bubble.png" alt="弹幕数量" >
                <span class="count">{{videoInfo.danmakus}}</span>
              </div>
            </el-tooltip>
          </div>
          <div class="tags">
            <el-tooltip effect="dark" content="视频标签" placement="bottom" v-for="tag in videoInfo.tags" :key="tag.id">
              <el-tag class="tag" @click="queryVideos(tag.name)">
                {{tag.name}}
              </el-tag>
            </el-tooltip>
          </div>
          <div class="description">
            <el-tooltip effect="dark" content="视频描述" placement="bottom">
              <p>
                {{ videoInfo.description }}
              </p>
            </el-tooltip>
          </div>
        </el-card>
      </div>
      <div>
        <el-card class="recommend" v-if="recommendList != null && recommendList.length > 0">
          <h3>相关视频</h3>
          <el-row :gutter="20" class="row">
            <el-col :span="6" :key="video.id" v-for="video in recommendList" >
              <div>
                <el-card :body-style="{ padding: '0px' }" shadow="hover">
                  <div class="image-container">
                    <el-image class="cover" :src="video.cover" @click="play(video.id)" />
                    <div class="image-text">
                      {{formatTime(video.duration)}}
                      <img class="icon" src="@/assets/view.png" alt="观看次数" > {{video.count}}
                      <img class="icon" src="@/assets/like-click.png" alt="点赞次数" > {{video.like}}
                      <img class="icon" src="@/assets/collect-click.png" alt="收藏数量" > {{video.collect}}
                    </div>
                  </div>
                  <div class="title"> {{video.title}} </div>
                  <div style="padding: 14px;">
                    <UserPopUp :avatar="video.avatar" :id="video.userId" :nick="video.nick"></UserPopUp>
                  </div>
                </el-card>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </div>
      <div class="comment-div">
        <el-card >
          <h4 class="comment-h4"> 评论 </h4>
          <el-input type="textarea" v-model="commentContent"></el-input>
          <div class="comment-button">
            <el-button  type="primary" @click="commentVideo">评论</el-button>
          </div>
        </el-card>
        <h4 class="comment-h4"> 评论列表 </h4>
        <el-empty v-if="commentList.length === 0" description="暂无评论"></el-empty>
        <el-card class="comment-card" :key="comment.id" v-for="comment in commentList" >
          <el-container>
            <el-aside width="200px">
              <div class="user-profile">
                <el-avatar :src="comment.avatar" />
                <span class="username" >{{comment.nick}}</span>
              </div>
            </el-aside>
            <el-container>
              <div class="comment-container">
                <div class="comment-content">{{comment.content}}</div>
                <div class="comment-time">{{comment.createTime}}</div>
                <div class="delete-button"
                     @click="deleteComment(comment.id)"
                     v-if="Global.user ? Global.user.id === comment.userId : false">删除</div>
              </div>
            </el-container>
          </el-container>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import dPlayer from 'vue-dplayer'
import videoApi from '@/api/video/video'
import Global from '@/components/Global.vue'
import UserPopUp from '@/components/user/UserPopUp.vue'

export default {
  name: 'VideoPlayer',
  components: {
    UserPopUp,
    dPlayer
  },
  data () {
    let that = this
    return {
      commentContent: null,
      isSubscribe: false,
      commentList: [],
      recommendList: [],
      viewers: 0,
      like: false,
      collect: false,
      videoId: this.$route.query.videoId,
      videoInfo: {},
      ws: null,
      dp: null,
      updateHistoryTimer: null,
      isPublish: true,
      options: {
        apiBackend: {
          read: function (options, callback) {
            // 这里可以自定义弹幕获取方式
            that.$http.get(options).then((res) => {
              callback('', res.data.data)
            })
          },
          send: function (url, data, callback) {
            if (Global.user == null) {
              that.$message.error('请登录后再发送')
              return
            }
            data.player = that.videoId
            data.userId = Global.user.id
            data.author = Global.user.nick
            that.ws.send(JSON.stringify(data))
            callback()
          }
        },
        // 视频地址
        video: {
          url: ''
        },
        danmaku: {
          id: '',
          api: '/api/video/danmakus/'
        }
      }
    }
  },
  beforeDestroy () {
    if (this.ws) {
      this.ws.close()
    }
    if (this.updateHistoryTimer) {
      clearInterval(this.updateHistoryTimer)
    }
  },
  mounted () {
    this.dp = this.$refs.player.dp
    this.getBasedItemRecommendList()
    videoApi.getVideoDetail(this.$route.query.videoId).then(res => {
      if (!res.data.playUrl) {
        this.isPublish = false
        this.videoInfo = res.data
        return
      }
      this.isPublish = true
      let that = this
      this.videoInfo = res.data
      if (Global.user != null) {
        videoApi.likeInfo(this.videoId).then(res => {
          this.like = res.data.isLike
          this.collect = res.data.isCollect
        })
        this.getHistory()
      }
      this.dp.switchVideo({
        url: res.data.playUrl,
        pic: res.data.cover
      }, {
        id: this.$route.query.videoId,
        api: '/api/video/danmakus/'
      })
      this.dp.on('play', function () {
        let timer = setTimeout(() => {
          videoApi.incrViewCounts(res.data.id)
          clearTimeout(timer)
        }, 1000)
        // 每秒记录一次播放位置
        that.updateHistoryTimer = setInterval(() => {
          that.updateHistory()
        }, 1000)
      })
      this.dp.on('pause', function () {
        if (that.updateHistoryTimer) {
          clearInterval(that.updateHistoryTimer)
        }
      })
    })
    this.getCommentList()
    let videoId = this.$route.query.videoId
    this.ws = new WebSocket(`ws://localhost:8080/danmaku/${videoId}`)
    this.ws.onopen = this.wsOpen
    this.ws.onmessage = this.wsMessage
    this.ws.onclose = this.wsClose
  },
  methods: {
    wsOpen () {
    },
    wsClose () {
      this.viewers--
    },
    wsMessage (message) {
      let res = JSON.parse(message.data)
      this.viewers = res.viewers
      if (res.text === null) {
        return
      }
      this.dp.danmaku.draw({
        text: res.text,
        color: res.color,
        type: res.type
      })
    },
    likeVideo () {
      videoApi.like(this.videoId).then(() => {
        this.like = !this.like
        let likeCount = this.videoInfo.like
        this.videoInfo.like = this.like ? parseInt(likeCount) + 1 : parseInt(likeCount) - 1
      })
    },
    collectVideo () {
      videoApi.collect(this.videoId).then(() => {
        this.collect = !this.collect
        let collectionCount = this.videoInfo.collect
        this.videoInfo.collect = this.collect ? collectionCount + 1 : collectionCount - 1
      })
    },
    getCommentList () {
      videoApi.getCommentList(this.videoId).then(res => {
        this.commentList = res.data
      })
    },
    commentVideo () {
      let content = this.commentContent
      if (content === null || content === '') {
        this.$message({
          type: 'error',
          message: '请输入内容发表评论'
        })
        return
      }
      let param = {
        videoId: this.videoId,
        content: content
      }
      videoApi.commentVideo(param).then(res => {
        this.commentContent = null
        this.getCommentList()
      })
    },
    deleteComment (commentId) {
      this.$confirm('确认删除该评论吗？', '删除评论').then(() => {
        videoApi.deleteComment(commentId).then(res => {
          this.$message('删除成功')
          this.getCommentList()
        })
      })
    },
    updateHistory () {
      if (Global.user !== null) {
        videoApi.updateHistory(this.videoId, this.dp.video.currentTime)
      }
    },
    getHistory () {
      videoApi.getHistory(this.videoId).then(res => {
        if (res.data !== -1) {
          this.$message('已为你跳转到上次的播放位置')
          this.dp.seek(res.data)
        }
      })
    },
    getBasedItemRecommendList () {
      videoApi.basedItemRecommend(this.videoId).then(res => {
        this.recommendList = res.data
      })
    },
    formatTime (seconds) {
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const remainingSeconds = Math.floor(seconds % 60)

      const formattedHours = hours.toString().padStart(2, '0')
      const formattedMinutes = minutes.toString().padStart(2, '0')
      const formattedSeconds = remainingSeconds.toString().padStart(2, '0')

      if (hours === 0) {
        return `${formattedMinutes}:${formattedSeconds}`
      } else {
        return `${formattedHours}:${formattedMinutes}:${formattedSeconds}`
      }
    },
    play (videoId) {
      this.$router.push({
        path: '/video-player',
        query: {
          videoId: videoId
        }
      })
    },
    queryVideos (tagName) {
      this.$router.push({
        name: 'Index',
        query: {
          keyword: tagName
        }
      })
    }
  },
  watch: {
    $route (to, from) {
      if (to.path === from.path && to.query !== from.query) {
        location.reload()
      }
    }
  }
}
</script>

<style scoped>
.video-title {
  text-align: left;
}
.player-div {
  display: flex;
  justify-content: center;
  padding: 0 10vw 5vh;
}

.player-card {
  position: relative;
  width: 70vw; /* 控制卡片宽度，可根据需要调整 */
  padding: 20px; /* 可根据需要调整内边距 */
}

.like-icon {
  cursor: pointer;
  max-width: 24px;
  margin-right: 5px;
}

.icon-container {
  margin: 20px auto;
  display: flex;
  align-items: center;
  gap: 20px;
}

.icon-item {
  display: flex;
  align-items: center;
  font-size: 18px;
  margin-right: 5px;
}

.count {
  font-weight: bold;
}

.comment-div {
  padding: 0 10vw 5vh;
}
.comment-h4 {
  text-align: left;
  margin-bottom: 10px;
}

.comment-button {
  text-align: left;
  margin-top: 10px;
}

.user-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.username {
  margin-top: 10px;
  font-weight: bold;
  text-align: center;
}

.comment-container {
  position: relative;
  display: flex;
  width: 100%;
  align-items: flex-start;
}

.comment-content {
  font-size: 16px;
  margin-right: 10px;
}

.delete-button {
  font-size: 14px;
  color: red;
  cursor: pointer;
  position: absolute;
  bottom: 0;
  margin: 0px 50px;
  right: 0;
}

.comment-card {
  margin-top: 8px
}

.comment-time {
  position: absolute;
  bottom: 0;
  left: 0;
  font-size: 12px;
  color: #999;
}

.description {
  padding: 10px;
  background-color: #f5f5f5;
  font-size: 16px;
  line-height: 1.5;
}

.user-info {
  display: flex;
  align-items: center;
}

.info-container {
  display: flex;
  align-items: center;
  margin-left: 10px;
}

.tags {
  margin: 10px 0;
}

.tag {
  margin: 5px 5px;
  cursor: pointer;
}

.row {
  margin-bottom: 20px;
}

.cover {
  max-height: 180px;
  width: 350px;
}

.recommend {
  margin: 16px 0;
}

.title {
  margin: auto 20px;
  font-size: 16px;
  font-weight: bold;

  width: 250px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: '...';
}
.icon {
  margin-left: 10px;
  max-width: 20px;
}

.image-container {
  position: relative;
  width: 100%; /* 调整图片容器的宽度 */
}

.image-container el-image {
  width: 100%;
  height: auto;
  display: block;
}

.image-text {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px;
  text-align: center;
  box-sizing: border-box;
  margin-bottom: 3px;
}
</style>
<style>
/*覆盖 dplayer 顶部、底部样式，解决弹幕位置错误问题*/
.dplayer-danmaku-bottom {
  position: absolute;
  width: 100%;
  text-align: center;
  visibility: hidden;
  left: 50%;
  transform: translateX(-50%);
}
.dplayer-danmaku-top {
  transform: translateX(-50%);
  position: absolute;
  width: 100%;
  text-align: center;
  visibility: hidden;
  left: 50%;
}
</style>
