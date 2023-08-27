package com.study.mydanmakuvideo.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mydanmakuvideo.modules.user.model.dto.UserDTO;
import com.study.mydanmakuvideo.modules.user.model.entity.UserSubscriptionEntity;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;

import java.util.List;

/**
 * <p>
 * 用户订阅表 服务类
 * </p>
 *
 * @author ocean
 * 
 */
public interface IUserSubscriptionService extends IService<UserSubscriptionEntity> {

    void subscribe(Long sourceUserId, Long authorUserId);

    void cancelSubscribe(Long sourceUserId, Long authorUserId);

    void publishVideo(Long videoId);

    void notifyUsersOfVideos();

    List<VideoDTO> getMsg(Long userId);

    void consumeMsg(Long userId, Integer index);

    Boolean isSubscription(Long currentUserId, Long userId);

    List<UserDTO> getSubscriptions(Long userId);

    List<UserDTO> getSubscribedList(Long userId);
}
