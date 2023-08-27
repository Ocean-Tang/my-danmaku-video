package com.study.mydanmakuvideo.modules.video.model.dto;

import com.study.mydanmakuvideo.common.annotation.validation.UploadFile;
import com.study.mydanmakuvideo.common.enums.VideoAreaEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author huangcanjie
 * 
 */
@ApiModel("上传视频DTO")
@Data
public class UploadVideoDTO {

    @ApiModelProperty("视频ID")
    private String videoId;

    @ApiModelProperty("视频上传文件")
    @UploadFile(max = 500, message = "视频文件不能超过 500 M")
    private MultipartFile videoFile;

    @ApiModelProperty("视频封面文件")
    @UploadFile(max = 5, message = "视频文件不能超过 5 M")
    private MultipartFile coverFile;

    @ApiModelProperty("视频封面")
    private String cover;

    @ApiModelProperty("用户ID")
    @NotNull(message = "用户信息不能为空")
    private Long userId;

    @ApiModelProperty("视频标题")
    @NotBlank(message = "视频标题不能为空")
    @Length(min = 4, max = 20, message = "视频标题长度应在4-20之间")
    private String title;

    @ApiModelProperty("视频描述")
    @Length(max = 500, message = "视频描述文本太长，请控制在500字符内")
    private String description;

    @ApiModelProperty("视频md5")
    @NotBlank(message = "视频数据缺失")
    private String md5;

    @ApiModelProperty("视频分区")
    @NotNull(message = "请选择视频分区")
    private VideoAreaEnum area;

    @ApiModelProperty("标签列表")
    private List<TagDTO> tags;
}
