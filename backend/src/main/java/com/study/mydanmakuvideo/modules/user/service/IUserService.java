package com.study.mydanmakuvideo.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mydanmakuvideo.common.dto.Token;
import com.study.mydanmakuvideo.modules.user.model.dto.UserDTO;
import com.study.mydanmakuvideo.modules.user.model.entity.UserEntity;
import com.study.mydanmakuvideo.modules.user.model.params.UserParam;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ocean
 * 
 */
public interface IUserService extends IService<UserEntity> {

    /**
     * 注册用户
     *
     * @param user
     */
    void signup(UserParam user);

    /**
     * 向邮箱发送验证码
     *
     * @param email
     */
    void sendCaptcha(String email);

    /**
     * 登录并返回token
     *
     * @param user
     * @return
     */
    Token login(UserParam user);

    /**
     * 生成 token
     *
     * @param id
     * @return
     */
    Token generateToken(Long id);

    /**
     * 刷新token
     *
     * @param refreshToken
     * @return
     */
    Token refreshToken(String refreshToken);

    UserDTO getInfoById(Long userId);

    void updateInfo(UserParam param);
}
