package com.study.mydanmakuvideo.modules.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.common.utils.JwtUtil;
import com.study.mydanmakuvideo.modules.video.mapper.VideoCommentMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoCommentDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoCommentEntity;
import com.study.mydanmakuvideo.modules.video.service.IVideoCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 视频评论表 服务实现类
 * </p>
 *
 * @author ocean
 * 
 */
@Service
public class VideoCommentServiceImpl extends ServiceImpl<VideoCommentMapper, VideoCommentEntity> implements IVideoCommentService {

    @Override
    public void comment(Long videoId, String content) {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        VideoCommentEntity entity = new VideoCommentEntity().setUserId(userId).setVideoId(videoId)
                .setContent(content);
        this.save(entity);
    }

    @Override
    public List<VideoCommentDTO> listByVideoId(Long videoId) {
        return baseMapper.listByVideoId(videoId);
    }
}
