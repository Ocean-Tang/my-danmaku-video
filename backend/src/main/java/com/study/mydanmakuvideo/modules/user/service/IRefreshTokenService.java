package com.study.mydanmakuvideo.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mydanmakuvideo.modules.user.model.entity.RefreshTokenEntity;

/**
 * <p>
 * 刷新令牌表 服务类
 * </p>
 *
 * @author ocean
 * 
 */
public interface IRefreshTokenService extends IService<RefreshTokenEntity> {


    /**
     * 设置 token
     *
     * @param userId
     * @param refreshToken
     */
    void setToken(Long userId, String refreshToken);

    /**
     * 退出登录
     * 删除数据库中对应的刷新令牌
     *
     * @param accessToken
     */
    void logout(String accessToken);

    /**
     * 是否存在指定的 刷新令牌
     *
     * @param userId
     * @param refreshToken
     * @return
     */
    boolean exitsRefreshToken(Long userId, String refreshToken);
}
