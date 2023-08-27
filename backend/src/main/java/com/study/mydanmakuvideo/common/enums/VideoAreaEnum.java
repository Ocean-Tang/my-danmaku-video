package com.study.mydanmakuvideo.common.enums;

import cn.hutool.core.util.EnumUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 视频分区枚举
 */
@AllArgsConstructor
@Getter
public enum VideoAreaEnum {

    LIFE(0, "生活"),
    CARTON(1, "动漫"),
    GAME(2, "游戏"),
    FASHION(3, "时尚"),
    MUSIC(4, "音乐"),
    TECHNOLOGY(5, "科技"),
    KNOWLEDGE(6, "知识"),
    SPORTS(7, "运动"),
    FOOD(8, "美食"),
    ENTERTAINMENT(9, "娱乐"),
    FILM(10, "影视");


    @EnumValue
    private final int code;
    @JsonValue
    private final String detail;

    public static Map<String, VideoAreaEnum> getCodeAndDetail() {
        LinkedHashMap<String, VideoAreaEnum> enumMap = EnumUtil.getEnumMap(VideoAreaEnum.class);
        return enumMap;
    }
}
