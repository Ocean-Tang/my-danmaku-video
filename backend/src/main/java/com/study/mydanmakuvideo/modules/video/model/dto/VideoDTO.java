package com.study.mydanmakuvideo.modules.video.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.mydanmakuvideo.common.enums.VideoAreaEnum;
import com.study.mydanmakuvideo.common.enums.VideoStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ocean
 * 
 */
@Data
@Accessors(chain = true)
@ApiModel("视频信息")
@Document(indexName = "video")
public class VideoDTO {

    @ApiModelProperty("视频ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户昵称")
    private String nick;

    @ApiModelProperty
    private String avatar;

    @ApiModelProperty("视频标题")
    private String title;

    @ApiModelProperty("视频描述")
    private String description;

    @ApiModelProperty("视频时长")
    private Float duration;

    @ApiModelProperty("阿里云视频ID")
    private String videoId;

    @ApiModelProperty("视频播放url")
    private String playUrl;

    @ApiModelProperty("封面url")
    private String cover;

    @ApiModelProperty("视频md5")
    private String md5;

    @ApiModelProperty("点赞数量")
    private Long like;

    @ApiModelProperty("收藏数量")
    private Integer collect;

    @ApiModelProperty("观看次数")
    private Integer count;

    @ApiModelProperty("弹幕数量")
    private Long danmakus;

    @ApiModelProperty("视频分区")
    @Field(type = FieldType.Nested)
    private VideoAreaEnum area;

    @ApiModelProperty("视频状态")
    private VideoStatusEnum status;

    @ApiModelProperty("发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @ApiModelProperty("历史记录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime viewTime;

    @ApiModelProperty("观看进度")
    private Integer progress;

    @ApiModelProperty("标签")
    @Field(type = FieldType.Nested)
    private List<TagDTO> tags;

    @ApiModelProperty("标签-字符串形式")
    private String tagsStr;
}
