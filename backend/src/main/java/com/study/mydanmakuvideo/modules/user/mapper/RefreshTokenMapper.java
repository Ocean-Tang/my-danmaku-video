package com.study.mydanmakuvideo.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mydanmakuvideo.modules.user.model.entity.RefreshTokenEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 刷新令牌表 Mapper 接口
 * </p>
 *
 * @author ocean
 * 
 */
@Mapper
public interface RefreshTokenMapper extends BaseMapper<RefreshTokenEntity> {

    /**
     * 删除 refreshToken
     *
     * @param userId
     */
    @Delete("delete from t_refresh_token where user_id = #{userId}")
    void logout(@Param("userId") Long userId);

    /**
     * 是否存在指定的刷新令牌
     *
     * @param userId
     * @param refreshToken
     * @return
     */
    @Select("select exists(select 1 from t_refresh_token where user_id = #{userId} and refresh_token = #{refreshToken} limit 1)")
    boolean existsRefreshToken(@Param("userId") Long userId,
                               @Param("refreshToken") String refreshToken);
}
