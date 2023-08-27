package com.study.mydanmakuvideo.modules.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.study.mydanmakuvideo.common.enums.GenderEnum;
import com.study.mydanmakuvideo.common.model.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author ocean
 * 
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_user")
public class UserEntity extends BaseEntity<UserEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 盐值
     */
    private String salt;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 个性签名
     */
    private String sign;

    /**
     * 性别，0男，1女，2-未知
     */
    private GenderEnum gender;

    /**
     * 生日
     */
    private LocalDate birth;

    @Override
    public Serializable pkVal() {
        return null;
    }
}
