package com.study.mydanmakuvideo.modules.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.study.mydanmakuvideo.modules.video.model.dto.UserPreference;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoEntity;
import com.study.mydanmakuvideo.modules.video.model.param.VideoQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 视频表 Mapper 接口
 * </p>
 *
 * @author ocean
 * 
 */
@Mapper
public interface VideoMapper extends BaseMapper<VideoEntity> {

    /**
     * 通过md5获取video
     *
     * @param md5
     * @return
     */
    @Select("select * from t_video where md5 = #{md5} and is_delete = 0 limit 1")
    VideoEntity getByMd5(@Param("md5") String md5);

    VideoDTO getVideoDetail(Long id);

    IPage<VideoDTO> pageVideo(@Param("page") IPage<VideoDTO> page, @Param("param") VideoQueryParam param);

    /**
     * 查询用户偏好数据
     *
     * @return
     */
    List<UserPreference> getPreferenceData();

    Long getPreferenceCountByUserId(@Param("userId") Long userId);

    List<VideoDTO> getRandomVideo(@Param("videoIds") List<Long> videoIds);

    Long getPreferenceCountByVideoId(Long videoId);
}
