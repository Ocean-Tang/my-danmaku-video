package com.study.mydanmakuvideo.modules.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoCollectionEntity;

import java.util.List;

/**
 * <p>
 * 视频收藏表 Mapper 接口
 * </p>
 *
 * @author ocean
 * 
 */
public interface VideoCollectionMapper extends BaseMapper<VideoCollectionEntity> {

    List<VideoDTO> getCollections(Long userId);
}
