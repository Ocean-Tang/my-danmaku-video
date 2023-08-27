<template>
  <div>
    <el-row v-for="(rowItems) in collections" :key="rowItems[0].id" :gutter="20" class="row">
      <el-col :span="6" v-for="(collection) in rowItems" :key="collection.id">
        <div>
          <el-card :body-style="{ padding: '0px' }" shadow="hover">
            <el-image class="cover" :src="collection.cover" @click="play(collection.id)"/>
            <div style="padding: 14px;">
              <span>{{ collection.title }}</span>
              <el-button type="warning" size="mini" @click="cancelCollection(collection.id)"> 取消收藏 </el-button>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import videoApi from '@/api/video/video'
export default {
  name: 'Collection',
  data () {
    return {
      collections: []
    }
  },
  mounted () {
    this.getCollections()
  },
  methods: {
    getCollections () {
      videoApi.getCollections().then(res => {
        this.collections = []
        for (let i = 0; i < res.data.length; i += 4) {
          this.collections.push(res.data.slice(i, i + 4))
        }
      })
    },
    play (id) {
      this.$router.push({
        path: '/video-player',
        query: {
          videoId: id
        }
      })
    },
    cancelCollection (id) {
      videoApi.collect(id).then(() => {
        this.getCollections().then()
      })
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
