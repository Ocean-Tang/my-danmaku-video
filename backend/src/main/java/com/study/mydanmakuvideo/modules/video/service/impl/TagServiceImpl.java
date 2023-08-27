package com.study.mydanmakuvideo.modules.video.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.modules.video.mapper.TagMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.TagDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.TagEntity;
import com.study.mydanmakuvideo.modules.video.service.ITagService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 视频标签表 服务实现类
 * </p>
 *
 * @author ocean
 * 
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, TagEntity> implements ITagService {

    @Override
    public List<TagDTO> getTags() {
        return baseMapper.getTags();
    }
}
