package com.study.mydanmakuvideo.modules.video.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 是否喜欢、收藏视频
 *
 * @author huangcanjie
 */
@Data
@ApiModel("是否喜欢、收藏视频DTO")
public class VideoLikeInfoDTO {
    @ApiModelProperty("是否喜欢")
    private Boolean isLike;
    @ApiModelProperty("是否收藏")
    private Boolean isCollect;
}
