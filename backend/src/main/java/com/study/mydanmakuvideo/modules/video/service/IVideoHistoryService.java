package com.study.mydanmakuvideo.modules.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoHistoryEntity;

import java.util.List;

/**
 * <p>
 * 视频观看历史记录 服务类
 * </p>
 *
 * @author ocean
 * 
 */
public interface IVideoHistoryService extends IService<VideoHistoryEntity> {

    void updateHistory(Long userId, Long videoId, Double time);

    Double getPlayPosition(Long userId, Long videoId);

    List<VideoDTO> histories(Long userId);
}
