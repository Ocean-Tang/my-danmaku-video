package com.study.mydanmakuvideo.modules.video.repository;

import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

/**
 * @author huangcanjie
 */
@Service
public interface VideoRepository extends ElasticsearchRepository<VideoDTO, Long> {


}
