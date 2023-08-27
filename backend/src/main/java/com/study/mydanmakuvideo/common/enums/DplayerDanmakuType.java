package com.study.mydanmakuvideo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Dplayer 弹幕类型
 */
@AllArgsConstructor
@Getter
public enum DplayerDanmakuType {

    top(1, "top"),
    right(0, "right"),
    bottom(2, "bottom");

    @EnumValue
    private final Integer code;
    private final String position;
}
