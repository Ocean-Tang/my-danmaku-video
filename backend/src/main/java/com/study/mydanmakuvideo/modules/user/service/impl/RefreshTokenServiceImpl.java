package com.study.mydanmakuvideo.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.common.utils.JwtUtil;
import com.study.mydanmakuvideo.modules.user.mapper.RefreshTokenMapper;
import com.study.mydanmakuvideo.modules.user.model.entity.RefreshTokenEntity;
import com.study.mydanmakuvideo.modules.user.service.IRefreshTokenService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 刷新令牌表 服务实现类
 * </p>
 *
 * @author ocean
 * 
 */
@Service
public class RefreshTokenServiceImpl extends ServiceImpl<RefreshTokenMapper, RefreshTokenEntity> implements IRefreshTokenService {

    @Override
    public void setToken(Long userId, String refreshToken) {
        RefreshTokenEntity entity = this.lambdaQuery()
                .eq(RefreshTokenEntity::getUserId, userId)
                .one();
        if (entity != null) {
            entity.setRefreshToken(refreshToken);
            this.updateById(entity);
        } else {
            entity = new RefreshTokenEntity()
                    .setRefreshToken(refreshToken)
                    .setUserId(userId);
            this.save(entity);
        }
    }

    @Override
    public void logout(String accessToken) {
        Long userId = JwtUtil.getIdOfPayload(accessToken);
        baseMapper.logout(userId);
    }

    @Override
    public boolean exitsRefreshToken(Long userId, String refreshToken) {
        return baseMapper.existsRefreshToken(userId, refreshToken);
    }
}
