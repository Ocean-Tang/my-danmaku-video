package com.study.mydanmakuvideo.modules.video.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.common.constant.RedisKeys;
import com.study.mydanmakuvideo.common.utils.JwtUtil;
import com.study.mydanmakuvideo.modules.video.mapper.VideoLikeMapper;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoLikeEntity;
import com.study.mydanmakuvideo.modules.video.service.IVideoLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频点赞表 服务实现类
 * </p>
 *
 * @author ocean
 * 
 */
@Service
@RequiredArgsConstructor
public class VideoLikeServiceImpl extends ServiceImpl<VideoLikeMapper, VideoLikeEntity> implements IVideoLikeService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Long getLikeCount(Long videoId) {
        String key = RedisKeys.VIDEO_LIKE + videoId;
        if (redisTemplate.hasKey(key)) {
            return redisTemplate.opsForSet().size(key);
        }
        Set<String> set = baseMapper.getUserIdSetByVideoId(videoId);
        if (set.size() == 0) {
            return 0L;
        }
        redisTemplate.opsForSet().add(key, set.toArray(new String[set.size()]));
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        return (long) set.size();
    }

    @Override
    public void likeVideo(Long videoId) {
        // 确保缓存存在 key
        this.getLikeCount(videoId);

        String key = RedisKeys.VIDEO_LIKE + videoId;
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        String userIdStr = userId.toString();
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
        if (opsForSet.isMember(key, userIdStr)) {
            opsForSet.remove(key, userIdStr);
        } else {
            opsForSet.add(key, userIdStr);
        }
    }

    @Override
    public boolean isLikeVideo(Long userId, Long videoId) {
        this.getLikeCount(videoId);
        return redisTemplate.opsForSet().isMember(RedisKeys.VIDEO_LIKE + videoId, userId.toString());
    }

    @Override
    public void saveOrUpdateLike(Long videoId, Set<Long> userIds) {
        Set<Long> likedUserIds = this.lambdaQuery().eq(VideoLikeEntity::getVideoId, videoId)
                .select(VideoLikeEntity::getUserId)
                .list()
                .stream().map(VideoLikeEntity::getUserId).collect(Collectors.toSet());
        Set<Long> unionUserIds = new HashSet<>(CollUtil.union(likedUserIds, userIds));
        Set<Long> removeUserIds = new HashSet<>(CollUtil.disjunction(unionUserIds, userIds));
        Set<Long> newUserIds = new HashSet<>(CollUtil.disjunction(unionUserIds, likedUserIds));

        if (CollUtil.isNotEmpty(removeUserIds)) {
            baseMapper.delRecord(videoId, removeUserIds);
        }
        List<VideoLikeEntity> entities = newUserIds.stream()
                .map(i -> new VideoLikeEntity().setVideoId(videoId).setUserId(i))
                .collect(Collectors.toList());
        this.saveBatch(entities);
    }
}
