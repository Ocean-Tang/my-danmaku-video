package com.study.mydanmakuvideo.modules.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoCommentDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoCommentEntity;

import java.util.List;

/**
 * <p>
 * 视频评论表 Mapper 接口
 * </p>
 *
 * @author ocean
 * 
 */
public interface VideoCommentMapper extends BaseMapper<VideoCommentEntity> {

    List<VideoCommentDTO> listByVideoId(Long videoId);
}
