package com.study.mydanmakuvideo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VideoStatusEnum {

    UPLOADING(0, "上传中"),
    TO_BE_REVIEWED(1, "待审核"),
    PUBLISHED(2, "已发布"),
    REMOVED(3, "已下架"),
    BLOCK(4, "审核不通过");

    @EnumValue
    private final int code;
    @JsonValue
    private final String detail;

}
