package com.study.mydanmakuvideo.modules.danmaku.model.dto;

import com.study.mydanmakuvideo.common.enums.DplayerDanmakuType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Dplayer 弹幕消息实体
 *
 * @author huangcanjie
 */
@Data
@Accessors(chain = true)
public class DplayerDanmakuDTO {

    private Long player;
    private String author;
    private Double time;
    private String text;
    private String color;
    private DplayerDanmakuType type;
    private String token;
    private Long userId;
    private String sessionId;
    private Long viewers;

    public Object[] toArrayInfo() {
        return new Object[]{time, type.getCode(), color, author, text};
    }
}
