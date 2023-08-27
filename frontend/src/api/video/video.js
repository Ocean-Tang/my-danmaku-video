import request from '../request'

const api = {
  upload (videoInfo) {
    let formDate = new FormData()
    formDate.append('videoFile', videoInfo.videoFile)
    formDate.append('coverFile', videoInfo.coverFile)
    formDate.append('md5', videoInfo.md5)
    formDate.append('userId', videoInfo.userId)
    formDate.append('title', videoInfo.title)
    formDate.append('description', videoInfo.description)
    formDate.append('area', videoInfo.area)
    for (let i = 0; i < videoInfo.tags.length; i++) {
      let tag = videoInfo.tags[i]
      formDate.append(`tags[${i}].name`, tag.name)
      if (tag.id) {
        formDate.append(`tags[${i}].id`, tag.id)
      }
    }
    return request({
      url: '/video/upload',
      headers: {
        'Content-Type': 'multipart/form-data' // 设置请求头为multipart/form-data
      },
      method: 'post',
      data: formDate,
      timeout: 3600000
    })
  },
  getVideoDetail (videoId) {
    return request({
      url: `/video/${videoId}`,
      method: 'get'
    })
  },
  incrViewCounts (videoId) {
    return request({
      url: `/video/incr-view-counts/${videoId}`,
      method: 'put'
    })
  },
  queryVideos (param) {
    return request({
      url: `/video`,
      params: param,
      method: 'get'
    })
  },
  like (videoId) {
    return request({
      url: `/video/like/${videoId}`,
      method: 'put'
    })
  },
  collect (videoId) {
    return request({
      url: `/video/collect/${videoId}`,
      method: 'put'
    })
  },
  likeInfo (videoId) {
    return request({
      url: `/video/like-info/${videoId}`,
      method: 'get'
    })
  },
  getCommentList (videoId) {
    return request({
      url: `/video/comments-list/${videoId}`,
      method: 'get'
    })
  },
  commentVideo (param) {
    return request({
      url: `/video/comment-video`,
      method: 'post',
      data: param
    })
  },
  deleteComment (commentId) {
    return request({
      url: `/video/comments/${commentId}`,
      method: 'delete'
    })
  },
  getAreas () {
    return request({
      url: `/video/areas`,
      method: 'get'
    })
  },
  getTags () {
    return request({
      url: `/video/tags`,
      method: 'get'
    })
  },
  recommend () {
    return request({
      url: `/video/recommend`,
      method: 'get'
    })
  },
  getHistory (videoId) {
    return request({
      url: `/video/history/${videoId}`,
      method: 'get'
    })
  },
  updateHistory (videoId, time) {
    return request({
      url: `/video/history/${videoId}/${time}`,
      method: 'put'
    })
  },
  getHistoryList () {
    return request({
      url: '/video/history',
      method: 'get'
    })
  },
  getCollections () {
    return request({
      url: '/video/collections',
      method: 'get'
    })
  },
  getWorks (userId) {
    return request({
      url: `/video/list/${userId}`,
      method: 'get'
    })
  },
  basedItemRecommend (videoId) {
    return request({
      url: `/video/recommend/${videoId}`,
      method: 'get'
    })
  }
}

export default api
