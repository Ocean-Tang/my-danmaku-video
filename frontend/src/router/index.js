import Vue from 'vue'
import Router from 'vue-router'
import Login from '../components/user/Login'
import Signup from '../components/user/Signup'
import Upload from '../components/video/Upload'
import VideoPlayer from '../components/video/VideoPlayer'
import Index from '../components/Index.vue'
import Detail from '@/components/user/Detail.vue'
import History from '@/components/user/History.vue'
import Collection from '@/components/user/Collection.vue'
import Works from '@/components/user/Works.vue'
import Subscription from '@/components/user/Subscription.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Index',
      component: Index
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/signup',
      name: 'signup',
      component: Signup
    },
    {
      path: '/upload-video',
      name: 'uploadVideo',
      component: Upload
    },
    {
      path: '/video-player',
      name: 'videoPlayer',
      component: VideoPlayer
    },
    {
      path: '/user/detail',
      name: 'userDetail',
      component: Detail
    },
    {
      path: '/user/history',
      name: 'history',
      component: History
    },
    {
      path: '/user/collection',
      name: 'collection',
      component: Collection
    },
    {
      path: '/user/works',
      name: 'works',
      component: Works
    },
    {
      path: '/user/subscription',
      name: 'subscription',
      component: Subscription
    },
    {
      path: '/user/subscribed',
      name: 'subscribed',
      component: Subscription
    }
  ]
})
