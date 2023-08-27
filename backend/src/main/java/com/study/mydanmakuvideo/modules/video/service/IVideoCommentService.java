package com.study.mydanmakuvideo.modules.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoCommentDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoCommentEntity;

import java.util.List;

/**
 * <p>
 * 视频评论表 服务类
 * </p>
 *
 * @author ocean
 * 
 */
public interface IVideoCommentService extends IService<VideoCommentEntity> {

    void comment(Long videoId, String content);

    List<VideoCommentDTO> listByVideoId(Long videoId);
}
