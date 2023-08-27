<template>
  <span>
    <el-dropdown>
      <span>
        <el-avatar v-if="avatar" :src="avatar" size="small"/>
        <el-link><span v-html="nick"></span></el-link>
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item >
          <el-button type="primary" size="mini" v-if="!isSubscribe" @click="subscribe()" >关注</el-button>
          <el-button type="warning" size="mini" v-if="isSubscribe" @click="cancelSubscribe()">取消关注</el-button>
        </el-dropdown-item>
        <el-dropdown-item ><div @click="getDetail">查看资料</div></el-dropdown-item>
        <el-dropdown-item ><div @click="works">作品列表</div></el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>

  </span>
</template>

<script>
import userApi from '@/api/user/user'
import Global from '@/components/Global.vue'
export default {
  name: 'UserPopUp',
  props: ['avatar', 'nick', 'id'],
  data () {
    return {
      isSubscribe: false
    }
  },
  mounted () {
    this.getIsSubscribe()
  },
  methods: {
    getIsSubscribe () {
      if (Global.user) {
        userApi.getIsSubscribe(this.id).then(res => {
          this.isSubscribe = res.data
        })
      }
    },
    subscribe () {
      userApi.subscribe(this.id).then(() => {
        this.getIsSubscribe()
        this.$message.success('关注成功')
      })
    },
    cancelSubscribe () {
      userApi.cancelSubscribe(this.id).then(() => {
        this.getIsSubscribe()
        this.$message.success('取消关注')
      })
    },
    getDetail () {
      this.$router.push({
        name: 'userDetail',
        query: {
          id: this.id
        }
      })
    },
    works () {
      this.$router.push({
        path: '/user/works',
        query: {
          id: this.id
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
