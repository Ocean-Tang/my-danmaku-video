package com.study.mydanmakuvideo.modules.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mydanmakuvideo.modules.video.model.dto.TagDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoTagEntity;

import java.util.List;

/**
 * <p>
 * 视频标签关联表 服务类
 * </p>
 *
 * @author ocean
 * 
 */
public interface IVideoTagService extends IService<VideoTagEntity> {

    void saveTags(Long videoId, List<TagDTO> tags);

    List<TagDTO> listByVideoId(Long id);
}
