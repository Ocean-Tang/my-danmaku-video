<template>
  <div>
    <h3>{{title}}</h3>
    <el-empty v-if="users.length === 0" description="暂无用户"></el-empty>
    <el-row v-for="row in users" :key="row[0].id">
      <el-col v-for="user in row" :key="user.id" :span="6">
        <el-card align="center">
          <UserPopUp :avatar="user.avatar" :id="user.id" :nick="user.nick"/>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import userApi from '@/api/user/user'
import UserPopUp from '@/components/user/UserPopUp.vue'
export default {
  name: 'Subscription',
  components: {UserPopUp},
  data () {
    return {
      title: '',
      users: []
    }
  },
  mounted () {
    if (this.$route.path === '/user/subscription') {
      this.getSubscriptions()
      this.title = '关注列表'
    } else {
      this.getSubscribedList()
      this.title = '粉丝列表'
    }
  },
  methods: {
    getSubscriptions () {
      userApi.getSubscriptions().then(res => {
        this.users = []
        for (let i = 0; i < res.data.length; i += 4) {
          this.users.push(res.data.slice(i, i + 4))
        }
      })
    },
    getSubscribedList () {
      userApi.getSubscribedList().then(res => {
        this.users = []
        for (let i = 0; i < res.data.length; i += 4) {
          this.users.push(res.data.slice(i, i + 4))
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
