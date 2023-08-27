package com.study.mydanmakuvideo.modules.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.modules.video.mapper.VideoHistoryMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoHistoryEntity;
import com.study.mydanmakuvideo.modules.video.service.IVideoHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 视频观看历史记录 服务实现类
 * </p>
 *
 * @author ocean
 * 
 */
@Service
public class VideoHistoryServiceImpl extends ServiceImpl<VideoHistoryMapper, VideoHistoryEntity> implements IVideoHistoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHistory(Long userId, Long videoId, Double time) {
        VideoHistoryEntity videoHistoryEntity = this.lambdaQuery()
                .eq(VideoHistoryEntity::getUserId, userId)
                .eq(VideoHistoryEntity::getVideoId, videoId)
                .oneOpt()
                .orElseGet(() -> new VideoHistoryEntity().setUserId(userId).setVideoId(videoId))
                .setTime(time);

        this.saveOrUpdate(videoHistoryEntity);
    }

    @Override
    public Double getPlayPosition(Long userId, Long videoId) {
        VideoHistoryEntity history = this.lambdaQuery()
                .eq(VideoHistoryEntity::getUserId, userId)
                .eq(VideoHistoryEntity::getVideoId, videoId)
                .one();
        return history == null ? -1 : history.getTime();
    }

    @Override
    public List<VideoDTO> histories(Long userId) {
        List<VideoDTO> list = baseMapper.histories(userId);
        return list;
    }
}
