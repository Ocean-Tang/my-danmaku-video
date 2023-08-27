package com.study.mydanmakuvideo.modules.video.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.study.mydanmakuvideo.common.enums.VideoAreaEnum;
import com.study.mydanmakuvideo.common.enums.VideoStatusEnum;
import com.study.mydanmakuvideo.common.model.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 视频表
 *
 * @author ocean
 * 
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_video")
public class VideoEntity extends BaseEntity<VideoEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 阿里云视频ID
     */
    private String videoId;

    /**
     * 视频MD5
     */
    private String md5;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频描述
     */
    private String description;

    private String cover;

    /**
     * 观看次数
     */
    private Long count;

    private Float duration;

    private VideoAreaEnum area;

    private VideoStatusEnum status;

    @Override
    public Serializable pkVal() {
        return null;
    }
}
