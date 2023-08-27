package com.study.mydanmakuvideo.modules.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.TagDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.TagEntity;

import java.util.List;

/**
 * <p>
 * 视频标签表 Mapper 接口
 * </p>
 *
 * @author ocean
 * 
 */
public interface TagMapper extends BaseMapper<TagEntity> {

    List<TagDTO> getTags();
}
