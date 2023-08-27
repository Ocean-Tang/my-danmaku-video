<template>
  <div class="upload">
    <el-form ref="form" :model="videoInfo" label-width="80px">
      <el-form-item label="视频文件">
        <el-upload
          class="upload-demo"
          accept="video/*"
          drag
          ref="videos"
          :file-list="fileList"
          action="http://localhost:8080/video/upload-video"
          :auto-upload=false
          :on-change="onChangeVideoFileHandler"
          :limit="1"
          multiple>
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <div class="el-upload__tip" slot="tip">只能上传 1 个视频文件，大小小于 500M</div>
        </el-upload>
      </el-form-item>
      <el-form-item label="封面">
        <el-upload
          action="/"
          accept="image/*"
          ref="covers"
          list-type="picture-card"
          :auto-upload=false
          :limit="1"
          :on-change="onChangeCoverFileHandler"
          :on-preview="handlePictureCardPreview">
          <i class="el-icon-plus"></i>
          <div class="el-upload__tip" slot="tip">只能上传 1 个封面</div>
        </el-upload>
        <el-dialog :visible.sync="dialogVisible">
          <img width="100%" :src="dialogImageUrl" alt="">
        </el-dialog>
      </el-form-item>
      <el-form-item label="视频名称" prop="title"
                    :rules="[
                      {required: true, message: '请输入标题', trigger: 'blur'},
                      { min: 4, max: 20, message: '长度在 4 到 20 个字符', trigger: 'blur' }
                    ]">
        <el-input v-model="videoInfo.title"></el-input>
      </el-form-item>
      <el-form-item label="视频分区" prop="area"
                    :rules="[
                      {required: true, message: '请选择分区', trigger: 'blur'}
                    ]">
        <el-select v-model="videoInfo.area" placeholder="请选择">
          <el-option
            v-for="(key, value) in areas"
            :key="key"
            :label="key"
            :value="value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="视频标签">
        <el-select v-model="videoInfo.tags"
                   placeholder="请选择视频标签"
                   filterable
                   multiple
                   value-key="id"
                   allow-create>
          <el-option
            v-for="tag in tags"
            :key="tag.id"
            :label="tag.name"
            :value="tag">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="视频描述">
        <el-input type="textarea" v-model="videoInfo.description"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="upload" v-loading.fullscreen.lock="fullscreenLoading">上传</el-button>
        <el-button>取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {MD5} from 'crypto-js'
import videoApi from '@/api/video/video'

export default {
  name: 'Upload',
  data () {
    return {
      videoInfo: {
        title: '',
        description: ''
      },
      fileList: [],
      dialogImageUrl: '',
      dialogVisible: false,
      areas: null,
      tags: null,
      fullscreenLoading: false
    }
  },
  mounted () {
    this.getAreas()
    this.getTags()
  },
  methods: {
    onChangeVideoFileHandler (file) {
      // 解决 element-ui 的 list 为空的情况
      console.log(this.$refs.videos)
      this.$refs.videos.uploadFiles = []
      this.$refs.videos.uploadFiles.push(file)
    },
    handlePictureCardPreview (file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    onChangeCoverFileHandler (file) {
      this.$refs.covers.uploadFiles = []
      this.$refs.covers.uploadFiles.push(file)
    },
    upload () {
      this.$refs.form.validate(valid => {
        if (valid) {
          console.log(this)
          for (let i = 0; i < this.videoInfo.tags.length; i++) {
            if ((typeof this.videoInfo.tags[i]) !== 'object') {
              this.videoInfo.tags[i] = {'name': this.videoInfo.tags[i]}
            }
          }
          if (!this.$refs.videos.uploadFiles[0]) {
            this.$message.error('请选择视频')
            return
          }
          let video = this.$refs.videos.uploadFiles[0].raw
          if (video.size >= 500 * 1024 * 1024) {
            this.$message.error('视频文件大小超过限制')
            return
          }
          if (!this.$refs.covers.uploadFiles[0]) {
            this.$message.error('请选择封面')
            return
          }
          let cover = this.$refs.covers.uploadFiles[0].raw
          if (cover.size >= 5 * 1024 * 1024) {
            this.$message.error('封面大小超过限制')
            return
          }
          if (!this.Global.user) {
            this.$message.error('请登录后再操作')
            return
          }
          let reader = new FileReader()
          reader.onload = () => {
            // 显示等待图标
            this.fullscreenLoading = true
            let fileContent = reader.result
            let md5 = MD5(fileContent).toString()

            this.videoInfo.userId = this.Global.user.id
            this.videoInfo.md5 = md5
            this.videoInfo.videoFile = video
            this.videoInfo.coverFile = cover
            // 上传视频完整信息
            videoApi.upload(this.videoInfo).then(res => {
              // 上传成功，跳转到详情页面
              this.$message.success('上传成功！')
              this.$router.push({
                path: '/video-player',
                query: {
                  videoId: res.data
                }
              })
            }).finally(() => { this.fullscreenLoading = false })
          }
          reader.readAsBinaryString(video)
        } else {
          this.$message.error('请填写完整信息')
        }
      })
    },
    getAreas () {
      videoApi.getAreas().then(res => {
        this.areas = res.data
      })
    },
    getTags () {
      videoApi.getTags().then(res => {
        this.tags = res.data
      })
    }
  }
}
</script>

<style scoped>
.upload {
  width: 60%;
}
</style>
