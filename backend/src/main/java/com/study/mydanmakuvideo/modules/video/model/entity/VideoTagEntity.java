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
 * 视频标签关联表
 * </p>
 *
 * @author ocean
 * 
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_video_tag")
@ApiModel(value = "VideoTagEntity对象", description = "视频标签关联表")
public class VideoTagEntity extends BaseEntity<VideoTagEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Long videoId;

    @ApiModelProperty("标签ID")
    private Long tagId;

    @Override
    public Serializable pkVal() {
        return null;
    }
}
