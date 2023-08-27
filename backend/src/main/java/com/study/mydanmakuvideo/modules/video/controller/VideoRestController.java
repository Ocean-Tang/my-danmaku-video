package com.study.mydanmakuvideo.modules.video.controller;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.study.mydanmakuvideo.common.annotation.aspect.ApiLimit;
import com.study.mydanmakuvideo.common.annotation.aspect.Login;
import com.study.mydanmakuvideo.common.controller.SuperController;
import com.study.mydanmakuvideo.common.dto.R;
import com.study.mydanmakuvideo.common.enums.VideoAreaEnum;
import com.study.mydanmakuvideo.common.utils.JwtUtil;
import com.study.mydanmakuvideo.modules.danmaku.service.IDanmakuService;
import com.study.mydanmakuvideo.modules.video.model.dto.TagDTO;
import com.study.mydanmakuvideo.modules.video.model.dto.UploadVideoDTO;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoCommentDTO;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.model.param.VideoQueryParam;
import com.study.mydanmakuvideo.modules.video.service.ITagService;
import com.study.mydanmakuvideo.modules.video.service.IVideoCollectionService;
import com.study.mydanmakuvideo.modules.video.service.IVideoCommentService;
import com.study.mydanmakuvideo.modules.video.service.IVideoLikeService;
import com.study.mydanmakuvideo.modules.video.service.IVideoService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 视频表 前端控制器
 *
 * @author ocean
 * 
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
@ApiLimit
public class VideoRestController extends SuperController {

    private final IVideoService videoService;
    private final IDanmakuService danmakuService;
    private final IVideoLikeService videoLikeService;
    private final IVideoCollectionService videoCollectionService;
    private final IVideoCommentService videoCommentService;
    private final ITagService tagService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Login
    @ApiOperation("上传视频")
    public R uploadVideo(@ModelAttribute @Validated UploadVideoDTO videoDTO) throws ClientException, IOException {
        return success(videoService.uploadVideo(videoDTO).toString());
    }

    /**
     * 吞吐量 40/s
     * 引入缓存后： 130/s
     */
    @ApiOperation("获取视频详情信息")
    @GetMapping("/{id}")
    @ApiLimit(count = 10)
    public R getVideoDetail(@PathVariable Long id) throws ClientException {
        return success(videoService.getVideoDetail(id));
    }

    @ApiOperation("获取当前用户是否对视频是否点赞、收藏")
    @GetMapping("/like-info/{id}")
    @Login
    public R getVideoLikeInfo(@PathVariable Long id) {
        return success(videoService.getVideoLikeInfo(id));
    }

    @GetMapping
    @ApiOperation("分页获取首页视频列表")
    @ApiLimit(count = 10)
    public R videoList(VideoQueryParam param) {
        Page<VideoDTO> page = new Page<>(param.getCurrent(), param.getSize());
        //IPage<VideoDTO> result = videoService.pageVideo(page, param);
        IPage<VideoDTO> result = videoService.queryVideosByEs(page, param);
        return success(result);
    }

    @ApiOperation("查询用户上传的视频列表")
    @GetMapping("/list/{userId}")
    public R userVideos(@PathVariable Long userId) {
        List<VideoDTO> list = videoService.userVideos(userId);
        return success(list);
    }

    @GetMapping("/danmakus/v2/")
    @ApiOperation("获取视频弹幕")
    public R getDanmakus(Long id) {
        List<Object[]> list = danmakuService.getDanmakusAsArrayInfoByVideoId(id);
        return this.success(list);
    }

    @ApiOperation("增加视频观看次数")
    @PutMapping("/incr-view-counts/{id}")
    public R incrViewCounts(@PathVariable("id") Long id) {
        videoService.incrViewCounts(id);
        return R.success();
    }

    @ApiOperation("点赞视频")
    @PutMapping("/like/{id}")
    @Login
    public R like(@PathVariable Long id) {
        videoLikeService.likeVideo(id);
        return R.success();
    }

    @ApiOperation("收藏视频")
    @PutMapping("/collect/{id}")
    @Login
    public R collect(@PathVariable Long id) {
        videoCollectionService.collect(id);
        return R.success();
    }

    @GetMapping("/collections")
    @Login
    @ApiOperation("用户的收藏列表")
    public R getCollections() {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        List<VideoDTO> list = videoCollectionService.getCollections(userId);
        return success(list);
    }

    @ApiOperation("获取视频的评论列表")
    @GetMapping("/comments-list/{id}")
    public R commentList(@PathVariable Long id) {
        List<VideoCommentDTO> videoCommentDTOS = videoCommentService.listByVideoId(id);
        return this.success(videoCommentDTOS);
    }

    @ApiOperation("评论视频")
    @PostMapping("/comment-video")
    @Login
    public R comment(@RequestBody VideoCommentDTO param) {
        videoCommentService.comment(param.getVideoId(), param.getContent());
        return R.success();
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/comments/{commentId}")
    @Login
    public R deleteComment(@PathVariable Long commentId) {
        videoCommentService.removeById(commentId);
        return R.success();
    }

    @ApiOperation("获取视频分区")
    @GetMapping("/areas")
    public R areas() {
        return this.success(VideoAreaEnum.getCodeAndDetail());
    }

    @ApiOperation("获取标签列表")
    @GetMapping("/tags")
    public R tags() {
        List<TagDTO> tags = tagService.getTags();
        return this.success(tags);
    }

    @ApiOperation("基于用户推荐视频")
    @GetMapping("/recommend")
    @Login
    public R recommendVideosUserBased() throws TasteException {
        List<VideoDTO> list = videoService.recommendVideosUserBased();
        return this.success(list);
    }

    @ApiOperation("基于内容推荐视频")
    @GetMapping("/recommend/{videoId}")
    public R recommendVideoItemBased(@PathVariable Long videoId) throws TasteException {
        List<VideoDTO> list = videoService.recommendVideoItemBased(videoId);
        return this.success(list);
    }

}
