package com.study.mydanmakuvideo.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("分页参数")
public class PageParam {

    private long current;
    private long size;

    public void setCurrent(long current) {
        this.current = current <= 0 ? 1 : current;
    }

    public void setSize(long size) {
        this.size = size <= 0 ? 20 : size;
    }
}
