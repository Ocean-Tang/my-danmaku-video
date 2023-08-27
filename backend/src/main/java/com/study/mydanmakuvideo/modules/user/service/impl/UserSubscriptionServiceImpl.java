package com.study.mydanmakuvideo.modules.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.common.constant.RedisKeys;
import com.study.mydanmakuvideo.modules.user.mapper.UserSubscriptionMapper;
import com.study.mydanmakuvideo.modules.user.model.dto.UserDTO;
import com.study.mydanmakuvideo.modules.user.model.entity.UserSubscriptionEntity;
import com.study.mydanmakuvideo.modules.user.service.IUserSubscriptionService;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.service.IVideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户订阅表 服务实现类
 * </p>
 *
 * @author ocean
 * 
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl extends ServiceImpl<UserSubscriptionMapper, UserSubscriptionEntity> implements IUserSubscriptionService {

    private final IVideoService videoService;
    private final LinkedBlockingQueue<Long> publishVideoIds = new LinkedBlockingQueue<>(30);
    private final ThreadPoolExecutor notifyUsersForVideosExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), ThreadUtil.createThreadFactory("subscribe-video-thread-"));
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void subscribe(Long sourceUserId, Long authorUserId) {
        UserSubscriptionEntity entity = this.lambdaQuery().eq(UserSubscriptionEntity::getUserId, sourceUserId)
                .eq(UserSubscriptionEntity::getAuthorId, authorUserId).one();
        Assert.isNull(entity, "已经关注该用户");
        entity = new UserSubscriptionEntity().setUserId(sourceUserId).setAuthorId(authorUserId);
        this.save(entity);
    }

    @Override
    public void cancelSubscribe(Long sourceUserId, Long authorUserId) {
        UserSubscriptionEntity entity = this.lambdaQuery().eq(UserSubscriptionEntity::getUserId, sourceUserId)
                .eq(UserSubscriptionEntity::getAuthorId, authorUserId).one();
        Assert.notNull(entity, "未关注该用户");
        this.remove(this.lambdaQuery().eq(UserSubscriptionEntity::getUserId, sourceUserId)
                .eq(UserSubscriptionEntity::getAuthorId, authorUserId)
                .getWrapper());
    }

    @Override
    public void publishVideo(Long videoId) {
        try {
            publishVideoIds.put(videoId);
        } catch (InterruptedException e) {
            log.error("发布新的视频到队列中，遇到中断，videoId：{}", videoId);
        }
    }

    @Override
    @PostConstruct
    public void notifyUsersOfVideos() {
        notifyUsersForVideosExecutor.execute(() -> {
            try {
                while (true) {
                    Long videoId = publishVideoIds.take();
                    log.info("订阅功能开始工作");
                    VideoDTO dto = videoService.getVideoNotPlayUrl(videoId);
                    List<Long> userIds = this.lambdaQuery().eq(UserSubscriptionEntity::getAuthorId, dto.getUserId())
                            .list().stream().map(UserSubscriptionEntity::getUserId)
                            .collect(Collectors.toList());
                    String msg = JSONUtil.toJsonStr(dto);
                    for (Long userId : userIds) {
                        String key = RedisKeys.SUBSCRIBE + userId;
                        redisTemplate.opsForList().leftPush(key, msg);
                        redisTemplate.expire(key, 7, TimeUnit.DAYS);
                    }
                }
            } catch (InterruptedException e) {
                log.error("从队列中获取视频ID时发生错误");
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<VideoDTO> getMsg(Long userId) {
        List<String> msgJson = CollUtil
                .emptyIfNull(redisTemplate.opsForList()
                        .range(RedisKeys.SUBSCRIBE + userId, 0, -1));
        return msgJson.stream().map(i -> JSONUtil.toBean(i, VideoDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void consumeMsg(Long userId, Integer index) {
        String key = RedisKeys.SUBSCRIBE + userId;
        String value = redisTemplate.opsForList().index(key, index);
        if (StrUtil.isBlank(value)) {
            return;
        }
        redisTemplate.opsForList().remove(key, 1, value);
    }

    @Override
    public Boolean isSubscription(Long currentUserId, Long userId) {
        return this.lambdaQuery().eq(UserSubscriptionEntity::getUserId, currentUserId)
                .eq(UserSubscriptionEntity::getAuthorId, userId)
                .count() > 0;
    }

    @Override
    public List<UserDTO> getSubscriptions(Long userId) {
        return baseMapper.getSubscriptions(userId);
    }

    @Override
    public List<UserDTO> getSubscribedList(Long userId) {
        return baseMapper.getSubscribedList(userId);
    }
}
