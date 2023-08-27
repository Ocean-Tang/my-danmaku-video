package com.study.mydanmakuvideo.modules.video.model.dto;

import lombok.Data;
import org.apache.mahout.cf.taste.model.Preference;

/**
 * 用户偏好
 *
 * @author huangcanjie
 */
@Data
public class UserPreference implements Preference {

    private long userId;
    private long videoId;
    private float value;

    @Override
    public long getUserID() {
        return this.userId;
    }

    @Override
    public long getItemID() {
        return this.videoId;
    }

    @Override
    public float getValue() {
        return this.value;
    }

    @Override
    public void setValue(float value) {
        this.value = value;
    }
}
