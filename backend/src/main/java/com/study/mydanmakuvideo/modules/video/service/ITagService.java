package com.study.mydanmakuvideo.modules.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mydanmakuvideo.modules.video.model.dto.TagDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.TagEntity;

import java.util.List;

/**
 * <p>
 * 视频标签表 服务类
 * </p>
 *
 * @author ocean
 * 
 */
public interface ITagService extends IService<TagEntity> {

    List<TagDTO> getTags();
}
