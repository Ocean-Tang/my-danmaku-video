package com.study.mydanmakuvideo.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Ocean
 * 
 * @apiNote
 */
@AllArgsConstructor
@Getter
public enum ReturnCodeEnums {

    /**
     * 成功
     */
    SUCCESS(200000, "成功"),
    /**
     * 失败
     */
    FAIL(400000, "失败"),
    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(400001, "验证码错误"),
    /**
     * 账号信息错误
     */
    NO_USER(400002, "没有该账号的用户"),
    /**
     * 密码错误
     */
    PASSWORD_ERROR(400003, "密码错误"),
    /**
     * 已存在的账号
     */
    EXITS_ACCOUNT(400004, "该账号已注册"),
    /**
     * token 无效
     */
    TOKEN_INVALID(400005, "token无效"),
    NO_LOGIN(400006, "未登录"),
    CANT_UPLOAD_AVATAR_TODAY(400007, "一天只能更改一次头像，请明天后重试"),
    REFRESH_TOKEN_INVALID(400008, "登录状态已经过期，请重新登陆"),

    // 接口限制访问
    LIMIT_ACCESS(400009, "访问过于频繁，请稍后重试"),
    // 视频相关
    UPLOAD_VIDEO_ERROR(500001, "上传视频失败"),
    VIDEO_LIMIT_SIZE(500002, "视频大小超过限制，大小超过500M"),
    UPLOAD_VIDEO_LIMIT(500003, "今日上传视频已经超过限制，请明天再试");

    /**
     * 响应码
     */
    private final Integer code;
    /**
     * 消息
     */
    private final String msg;
}
