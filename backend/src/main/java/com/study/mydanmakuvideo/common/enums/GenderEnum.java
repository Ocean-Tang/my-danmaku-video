package com.study.mydanmakuvideo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GenderEnum implements BaseEnum {

    FEMALE(0, "女"),
    MALE(1, "男"),
    UNKNOWN(2, "未知");

    @EnumValue
    private final int code;
    @JsonValue
    private final String detail;
}
