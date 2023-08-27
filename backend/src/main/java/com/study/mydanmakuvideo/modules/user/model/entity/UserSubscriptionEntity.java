package com.study.mydanmakuvideo.modules.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.study.mydanmakuvideo.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户订阅表
 * </p>
 *
 * @author ocean
 * 
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_user_subscription")
@ApiModel(value = "UserSubscriptionEntity对象", description = "用户订阅表")
public class UserSubscriptionEntity extends BaseEntity<UserSubscriptionEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("被订阅用户ID")
    private Long authorId;

    @Override
    public Serializable pkVal() {
        return null;
    }
}
