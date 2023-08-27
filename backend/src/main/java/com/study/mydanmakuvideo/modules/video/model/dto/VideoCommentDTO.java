package com.study.mydanmakuvideo.modules.video.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频评论DTO
 *
 * @author huangcanjie
 */
@ApiModel("视频评论DTO")
@Data
public class VideoCommentDTO {

    @ApiModelProperty("主键")
    private Long id;
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("昵称")
    private String nick;
    @ApiModelProperty("头像")
    private String avatar;
    @ApiModelProperty("视频ID")
    private Long videoId;
    @ApiModelProperty("评论内容")
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
