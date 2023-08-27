package com.study.mydanmakuvideo.modules.video.service;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mydanmakuvideo.modules.video.model.dto.UploadVideoDTO;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoLikeInfoDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoEntity;
import com.study.mydanmakuvideo.modules.video.model.param.VideoQueryParam;
import org.apache.mahout.cf.taste.common.TasteException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 视频表 服务类
 * </p>
 *
 * @author ocean
 * 
 */
public interface IVideoService extends IService<VideoEntity> {

    /**
     * 上传视频
     *
     * @param videoDTO      视频上传参数
     * @return 数据库视频ID
     */
    Long uploadVideo(UploadVideoDTO videoDTO) throws ClientException, IOException;

    VideoDTO getVideoDetail(Long id) throws ClientException;

    IPage<VideoDTO> pageVideo(IPage<VideoDTO> page, VideoQueryParam param);

    void incrViewCounts(Long id);


    VideoLikeInfoDTO getVideoLikeInfo(Long videoId);

    List<VideoDTO> recommendVideosUserBased() throws TasteException;

    List<VideoDTO> recommendVideoItemBased(Long videoId) throws TasteException;

    VideoDTO getVideoNotPlayUrl(Long videoId);

    List<VideoDTO> userVideos(Long userId);

    IPage<VideoDTO> queryVideosByEs(Page<VideoDTO> page, VideoQueryParam param);

    void saveToEsById(Long videoId);

    void saveToEsByIdBatch(Set<Long> videoIds);
}
