package com.study.mydanmakuvideo.modules.video.model.param;

import com.study.mydanmakuvideo.common.dto.PageParam;
import com.study.mydanmakuvideo.common.enums.VideoAreaEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("视频查询参数")
@Data
public class VideoQueryParam extends PageParam {

    private VideoAreaEnum area;
    private String keyword;
    private long seed;

}
