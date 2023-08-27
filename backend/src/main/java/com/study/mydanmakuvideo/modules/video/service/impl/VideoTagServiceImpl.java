package com.study.mydanmakuvideo.modules.video.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.modules.video.mapper.VideoTagMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.TagDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.TagEntity;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoTagEntity;
import com.study.mydanmakuvideo.modules.video.service.ITagService;
import com.study.mydanmakuvideo.modules.video.service.IVideoTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频标签关联表 服务实现类
 * </p>
 *
 * @author ocean
 * 
 */
@Service
@RequiredArgsConstructor
public class VideoTagServiceImpl extends ServiceImpl<VideoTagMapper, VideoTagEntity> implements IVideoTagService {

    private final ITagService tagService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTags(Long videoId, List<TagDTO> tags) {
        List<TagEntity> tagEntities = BeanUtil.copyToList(tags, TagEntity.class);
        tagService.saveOrUpdateBatch(tagEntities);
        this.remove(this.lambdaQuery().eq(VideoTagEntity::getVideoId, videoId).getWrapper());
        List<VideoTagEntity> videoTagEntities = tagEntities.stream()
                .map(i -> new VideoTagEntity().setTagId(i.getId()).setVideoId(videoId))
                .collect(Collectors.toList());
        this.saveOrUpdateBatch(videoTagEntities);
    }

    @Override
    public List<TagDTO> listByVideoId(Long id) {
        return baseMapper.selectListByVideoId(id);
    }
}
