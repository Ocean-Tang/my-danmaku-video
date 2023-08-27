package com.study.mydanmakuvideo.modules.video.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.study.mydanmakuvideo.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 视频观看历史记录
 * </p>
 *
 * @author ocean
 * 
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_video_history")
@ApiModel(value = "VideoHistoryEntity对象", description = "视频观看历史记录")
public class VideoHistoryEntity extends BaseEntity<VideoHistoryEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("视频ID")
    private Long videoId;

    @ApiModelProperty("视频时间位置")
    private Double time;

    @Override
    public Serializable pkVal() {
        return null;
    }
}
