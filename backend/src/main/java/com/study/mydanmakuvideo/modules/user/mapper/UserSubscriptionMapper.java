package com.study.mydanmakuvideo.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.mydanmakuvideo.modules.user.model.dto.UserDTO;
import com.study.mydanmakuvideo.modules.user.model.entity.UserSubscriptionEntity;

import java.util.List;

/**
 * <p>
 * 用户订阅表 Mapper 接口
 * </p>
 *
 * @author ocean
 * 
 */
public interface UserSubscriptionMapper extends BaseMapper<UserSubscriptionEntity> {

    List<UserDTO> getSubscriptions(Long userId);

    List<UserDTO> getSubscribedList(Long userId);
}
