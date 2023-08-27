package com.study.mydanmakuvideo.modules.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.study.mydanmakuvideo.common.model.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 刷新令牌表
 * </p>
 *
 * @author ocean
 * 
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_refresh_token")
public class RefreshTokenEntity extends BaseEntity<RefreshTokenEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    @Override
    public Serializable pkVal() {
        return null;
    }
}
