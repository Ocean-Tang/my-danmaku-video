<template>
  <div>
    <div v-if="Global.user">
      <h3>为你推荐</h3>
      <el-carousel  :interval="4000" type="card" height="200px">
        <el-carousel-item v-for="video in recommendList" :key="video.id" >
          <el-tooltip effect="dark" :content="video.title" placement="top" >
            <el-image @click="playRecommend(video.id)" :src="video.cover"></el-image>
          </el-tooltip>
        </el-carousel-item>
      </el-carousel>
    </div>
    <div class="searchDiv">
      <div class="searchInput">
        <span>
          <el-input
            style="width: 40%"
            placeholder="请输入搜索内容，使用空格分隔关键词，支持搜索标签"
            suffix-icon="el-icon-search"
            @keyup.enter="queryVideos"
            v-model="param.keyword">
        </el-input>
          <el-button @click="queryVideos" style="width: 5%" type="primary">搜索</el-button>
        </span>
      </div>
      <el-radio-group v-model="param.area" @input="changeArea()">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button :key="key" v-for="(value, key) in areas"
                         :label="key" >{{value}}</el-radio-button>
      </el-radio-group>
    </div>
    <div>
      <el-row v-for="(rowItems, rowIndex) in chunkedRecords" :key="rowIndex" :gutter="20" class="row">
        <el-col :span="6" v-for="(record, colIndex) in rowItems" :key="colIndex">
          <div>
            <el-card :body-style="{ padding: '0px' }" shadow="hover">
              <div class="image-container">
                <el-image class="cover" :lazy="true" :src="record.cover" @click="play(rowIndex, colIndex)"/>
                <div class="image-text">
                  {{formatTime(record.duration)}}
                  <img class="icon" src="@/assets/view.png" alt="观看次数" > {{record.count}}
                  <img class="icon" src="@/assets/like-click.png" alt="点赞次数" > {{record.like}}
                  <img class="icon" src="@/assets/collect-click.png" alt="收藏数量" > {{record.collect}}
                </div>
              </div>
              <div class="title" v-html="record.title"></div>
              <div style="padding: 14px;">
                <UserPopUp :avatar="record.avatar" :id="record.userId" :nick="record.nick"></UserPopUp>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
    <el-pagination
      ref="pagination"
      background
      layout="prev, pager, next"
      @current-change="changePage"
      :page-count="param.pages">
    </el-pagination>
  </div>
</template>

<script>
import videoApi from '@/api/video/video'
import Global from '@/components/Global.vue'
import UserPopUp from '@/components/user/UserPopUp.vue'

export default {
  name: 'Index',
  components: {UserPopUp},
  data () {
    return {
      param: {
        keyword: null,
        area: null,
        seed: this.getRandomInt(Number.MIN_SAFE_INTEGER, Number.MAX_SAFE_INTEGER),
        current: 1,
        size: 20,
        pages: 1
      },
      recommendList: [],
      records: [],
      areas: null
    }
  },
  mounted () {
    if (this.$route.query && this.$route.query.keyword) {
      this.param.keyword = this.$route.query.keyword
    }
    this.getAreas()
    this.queryVideos()
    this.recommend()
  },
  computed: {
    chunkedRecords () {
      // 将一维数组按每行四个元素划分为二维数组
      const chunkSize = 4
      const chunked = []
      for (let i = 0; i < this.records.length; i += chunkSize) {
        chunked.push(this.records.slice(i, i + chunkSize))
      }
      return chunked
    }
  },
  methods: {
    queryVideos () {
      videoApi.queryVideos(this.param).then(res => {
        console.log(res.data, this.param)
        let data = res.data
        this.param.current = parseInt(data.current)
        this.param.pages = parseInt(data.pages)
        this.records = []
        this.records = data.records
        console.log(this.records)
      })
    },
    changePage (current) {
      this.param.current = current
      this.queryVideos()
    },
    play (i, j) {
      this.playRecommend(this.chunkedRecords[i][j].id)
    },
    playRecommend (id) {
      this.$router.push({
        path: '/video-player',
        query: {
          videoId: id
        }
      })
    },
    getAreas () {
      videoApi.getAreas().then(res => {
        console.log(res.data)
        this.areas = res.data
      })
    },
    changeArea () {
      this.queryVideos()
    },
    recommend () {
      if (Global.user) {
        videoApi.recommend().then(res => {
          console.log(res.data)
          this.recommendList = res.data
        })
      }
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
    getRandomInt (min, max) {
      return Math.floor(Math.random() * (max - min + 1)) + min
    }
  }
}
</script>

<style scoped>
.row {
  margin-bottom: 20px;
}

.cover {
  max-height: 180px;
  width: 350px;
}

.searchDiv {
  text-align: center;
  margin: 20px 20px;
}

.searchInput {
  margin: 16px 16px
}

.el-card {
  height: 260px;
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
.keyword {
  color: red;
}
</style>
