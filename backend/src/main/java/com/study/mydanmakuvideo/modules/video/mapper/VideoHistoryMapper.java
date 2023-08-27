package com.study.mydanmakuvideo.modules.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoHistoryEntity;

import java.util.List;

/**
 * <p>
 * 视频观看历史记录 Mapper 接口
 * </p>
 *
 * @author ocean
 * 
 */
public interface VideoHistoryMapper extends BaseMapper<VideoHistoryEntity> {

    List<VideoDTO> histories(Long userId);
}
