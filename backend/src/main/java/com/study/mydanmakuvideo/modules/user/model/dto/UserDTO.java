package com.study.mydanmakuvideo.modules.user.model.dto;

import com.study.mydanmakuvideo.common.enums.GenderEnum;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户信息
 * @author huangcanjie
 * 
 */
@Data
public class UserDTO {

    private Long id;

    /**
     * 邮箱
     */
    private String email;

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
     * 性别，0女，1男，2-未知
     */
    private GenderEnum gender;

    /**
     * 生日
     */
    private LocalDate birth;
}
