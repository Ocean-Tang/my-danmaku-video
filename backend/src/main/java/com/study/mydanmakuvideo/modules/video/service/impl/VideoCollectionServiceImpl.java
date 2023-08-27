package com.study.mydanmakuvideo.modules.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.common.utils.JwtUtil;
import com.study.mydanmakuvideo.modules.video.mapper.VideoCollectionMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoCollectionEntity;
import com.study.mydanmakuvideo.modules.video.service.IVideoCollectionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 视频收藏表 服务实现类
 * </p>
 *
 * @author ocean
 * 
 */
@Service
public class VideoCollectionServiceImpl extends ServiceImpl<VideoCollectionMapper, VideoCollectionEntity> implements IVideoCollectionService {

    @Override
    public void collect(Long videoId) {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        VideoCollectionEntity entity = this.lambdaQuery().eq(VideoCollectionEntity::getUserId, userId)
                .eq(VideoCollectionEntity::getVideoId, videoId)
                .one();
        if (entity == null) {
            entity = new VideoCollectionEntity()
                    .setUserId(userId)
                    .setVideoId(videoId);
            this.save(entity);
        } else {
            this.removeById(entity.getId());
        }
    }

    @Override
    public boolean isCollect(Long userId, Long videoId) {
        Long count = this.lambdaQuery().eq(VideoCollectionEntity::getUserId, userId)
                .eq(VideoCollectionEntity::getVideoId, videoId)
                .count();
        return count > 0;
    }

    @Override
    public List<VideoDTO> getCollections(Long userId) {
        return baseMapper.getCollections(userId);
    }
}
