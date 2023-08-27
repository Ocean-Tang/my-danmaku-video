package com.study.mydanmakuvideo.modules.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoLikeEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * <p>
 * 视频点赞表 Mapper 接口
 * </p>
 *
 * @author ocean
 * 
 */
public interface VideoLikeMapper extends BaseMapper<VideoLikeEntity> {

    @Select("select user_id from t_video_like where video_id = #{videoId}")
    Set<String> getUserIdSetByVideoId(Long videoId);

    void delRecord(@Param("videoId") Long videoId, @Param("removeUserIds") Set<Long> removeUserIds);
}
