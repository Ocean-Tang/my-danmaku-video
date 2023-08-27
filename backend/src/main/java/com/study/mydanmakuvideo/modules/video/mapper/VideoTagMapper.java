package com.study.mydanmakuvideo.modules.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.TagDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoTagEntity;

import java.util.List;

/**
 * <p>
 * 视频标签关联表 Mapper 接口
 * </p>
 *
 * @author ocean
 * 
 */
public interface VideoTagMapper extends BaseMapper<VideoTagEntity> {

    List<TagDTO> selectListByVideoId(Long id);
}
