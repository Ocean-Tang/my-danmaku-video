package com.study.mydanmakuvideo.modules.video.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huangcanjie
 */
@Data
@ApiModel("AI审核任务DTO")
@AllArgsConstructor
public class AiReviewJobDTO {

    @ApiModelProperty("任务ID")
    private String jobId;
    @ApiModelProperty("数据库视频ID")
    private Long videoId;
    @ApiModelProperty("阿里云视频ID")
    private String aliyunVideoId;
}
