package com.study.mydanmakuvideo.modules.danmaku.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.study.mydanmakuvideo.common.enums.DplayerDanmakuType;
import com.study.mydanmakuvideo.common.model.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 弹幕表
 * </p>
 *
 * @author ocean
 * 
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_danmaku")
public class DanmakuEntity extends BaseEntity<DanmakuEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 相对时间
     */
    private Double time;

    /**
     * 弹幕位置
     */
    private DplayerDanmakuType position;

    /**
     * 弹幕颜色
     */
    private String color;

    /**
     * 用户昵称
     */
    private String nick;

    /**
     * 弹幕内容
     */
    private String content;

    @Override
    public Serializable pkVal() {
        return null;
    }

    public Object[] toArrayInfo() {
        return new Object[]{time, position.getCode(), color, nick, content};
    }
}
