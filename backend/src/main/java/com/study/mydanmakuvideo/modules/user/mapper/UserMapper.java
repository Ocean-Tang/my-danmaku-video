package com.study.mydanmakuvideo.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mydanmakuvideo.modules.user.model.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ocean
 * 
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}
