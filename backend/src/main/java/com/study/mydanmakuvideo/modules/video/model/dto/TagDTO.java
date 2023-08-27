package com.study.mydanmakuvideo.modules.video.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("标签")
@Data
public class TagDTO {

    @ApiModelProperty("id")
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
}
