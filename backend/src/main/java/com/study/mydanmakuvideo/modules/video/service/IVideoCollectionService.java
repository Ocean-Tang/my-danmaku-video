package com.study.mydanmakuvideo.modules.video.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoCollectionEntity;

import java.util.List;

/**
 * <p>
 * 视频收藏表 服务类
 * </p>
 *
 * @author ocean
 * 
 */
public interface IVideoCollectionService extends IService<VideoCollectionEntity> {

    void collect(Long videoId);

    boolean isCollect(Long userId, Long videoId);

    List<VideoDTO> getCollections(Long userId);
}
