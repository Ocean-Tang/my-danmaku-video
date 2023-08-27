<template>
  <div>
    <el-row v-for="(rowItems) in works" :key="rowItems[0].id" :gutter="20" class="row">
      <el-col :span="6" v-for="(work) in rowItems" :key="work.id">
        <div>
          <el-card :body-style="{ padding: '0px' }" shadow="hover">
            <el-image class="cover" :src="work.cover" @click="play(work.id)"/>
            <div style="padding: 14px;">
              <span>{{ work.title }}</span>
              <el-tag v-if="showStatus" :type="getStatusType(work.status)">{{work.status}}</el-tag>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import videoApi from '@/api/video/video'
import Global from '@/components/Global.vue'
import {stat} from 'copy-webpack-plugin/dist/utils/promisify'

export default {
  name: 'Works',
  computed: {
    Global () {
      return Global
    }
  },
  data () {
    return {
      works: [],
      showStatus: false
    }
  },
  mounted () {
    this.getWorks()
    this.setShowStatus()
  },
  methods: {
    getWorks () {
      videoApi.getWorks(this.$route.query.id).then(res => {
        this.works = []
        for (let i = 0; i < res.data.length; i += 4) {
          this.works.push(res.data.slice(i, i + 4))
        }
      })
    },
    setShowStatus () {
      this.showStatus = Global.user && Global.user.id === this.$route.query.id
    },
    play (id) {
      this.$router.push({
        path: '/video-player',
        query: {
          videoId: id
        }
      })
    },
    getStatusType (status) {
      switch (status) {
        case '上传中':
        case '待审核': return 'warning'
        case '已发布': return 'success'
        case '已下架': return 'info'
        case '审核不通过': return 'danger'
      }
    }
  }
}
</script>

<style scoped>
.row {
  margin-bottom: 20px;
}

.cover {
  max-height: 200px;
  width: 350px;
}
</style>
