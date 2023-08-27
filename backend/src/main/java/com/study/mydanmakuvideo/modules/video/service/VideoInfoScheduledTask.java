package com.study.mydanmakuvideo.modules.video.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;
import com.study.mydanmakuvideo.common.constant.AliyunAiJobStatusConstant;
import com.study.mydanmakuvideo.common.constant.RedisKeys;
import com.study.mydanmakuvideo.common.enums.VideoStatusEnum;
import com.study.mydanmakuvideo.common.model.entity.BaseEntity;
import com.study.mydanmakuvideo.modules.danmaku.model.dto.DplayerDanmakuDTO;
import com.study.mydanmakuvideo.modules.danmaku.model.entity.DanmakuEntity;
import com.study.mydanmakuvideo.modules.danmaku.service.IDanmakuService;
import com.study.mydanmakuvideo.modules.oss.service.IVodService;
import com.study.mydanmakuvideo.modules.user.service.IUserSubscriptionService;
import com.study.mydanmakuvideo.modules.video.model.dto.AiReviewJobDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 视频相关信息定时任务类
 *
 * @author huangcanjie
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VideoInfoScheduledTask {

    private final RedisTemplate<String, String> redisTemplate;
    private final IVideoService videoService;
    private final IVodService vodService;
    private final IUserSubscriptionService userSubscriptionService;


    private final ThreadPoolExecutor persistenceDanmakuExecutor = new ThreadPoolExecutor(1,
            5,
            1,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(10),
            ThreadFactoryBuilder.create().setNamePrefix("persistence-danmaku").build());

    private final IDanmakuService danmakuService;
    private final IVideoLikeService videoLikeService;

    @Scheduled(cron = "0 0,30 * * * ?")
    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void syncVideoData() {
        Set<Long> videoIds1 = syncLikeCounts();
        Set<Long> videoIds2 = syncViewCounts();
        syncDanmakus();

        videoIds1.addAll(videoIds2);
        videoService.saveToEsByIdBatch(videoIds1);
        for (Long id : videoIds1) {
            redisTemplate.delete(RedisKeys.VIDEO_DETAIL + id);
        }
    }

    public Set<Long> syncViewCounts() {
        log.info("开始同步视屏观看次数");
        Map<Long, VideoEntity> entityMap = new HashMap<>();
        Set<String> viewCounts = redisTemplate.keys(RedisKeys.VIEW_COUNTS + "*");
        for (String key : viewCounts) {
            Long videoId = Long.valueOf(key.split(":")[2]);
            VideoEntity videoEntity;
            if (!entityMap.containsKey(videoId)) {
                videoEntity = new VideoEntity();
                videoEntity.setId(videoId);
            } else {
                videoEntity = entityMap.get(videoId);
            }
            videoEntity.setCount(Long.valueOf(redisTemplate.opsForValue().getAndDelete(key)));
            entityMap.put(videoId, videoEntity);
        }
        videoService.updateBatchById(entityMap.values());
        log.info("结束同步视屏观看次数");
        return entityMap.values().stream().map(BaseEntity::getId).collect(Collectors.toSet());
    }

    public Set<Long> syncLikeCounts() {
        log.info("开始同步视频点赞数据");
        Set<String> keys = redisTemplate.keys(RedisKeys.VIDEO_LIKE + "*");
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();

        Set<Long> videoIds = new HashSet<>();
        for (String key : keys) {
            Set<String> userIdStrSet = opsForSet.members(key);
            Long videoId = Long.valueOf(key.split(":")[2]);
            Set<Long> userIds = userIdStrSet.stream().map(Long::valueOf).collect(Collectors.toSet());
            videoLikeService.saveOrUpdateLike(videoId, userIds);
            videoIds.add(videoId);
        }
        log.info("结束同步视频点赞数据");
        return videoIds;
    }

    public void syncDanmakus() {
        Set<String> keys = redisTemplate.keys(RedisKeys.DANMAKU_NEW.concat("*"));
        log.info("定时持久化弹幕数据，共有{}个视频的弹幕需要持久化", keys.size());

        for (String key : keys) {
            List<String> values = redisTemplate.opsForList().leftPop(key, redisTemplate.opsForList().size(key));
            if (CollectionUtil.isEmpty(values)) {
                continue;
            }

            persistenceDanmakuExecutor.execute(() -> {
                log.info("{}开始持久化弹幕，共有{}条弹幕", Thread.currentThread().getName(), values.size());
                List<DanmakuEntity> entities = new ArrayList<>();
                for (String value : values) {
                    DplayerDanmakuDTO dto = JSONUtil.toBean(value, DplayerDanmakuDTO.class);
                    DanmakuEntity danmakuEntity = new DanmakuEntity()
                            .setTime(dto.getTime())
                            .setNick(dto.getAuthor())
                            .setColor(dto.getColor())
                            .setContent(dto.getText())
                            .setPosition(dto.getType())
                            .setUserId(dto.getUserId())
                            .setVideoId(dto.getPlayer());
                    entities.add(danmakuEntity);
                }
                danmakuService.saveBatch(entities);
                log.info("{}完成弹幕持久化", Thread.currentThread().getName());
            });
        }
    }

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void updateVideoReviewStatus() throws ClientException {
        ListOperations<String, String> opsForList = redisTemplate.opsForList();
        Long size = opsForList.size(RedisKeys.AI_REVIEW);
        for (int i = 0; i < size; i++) {
            String jsonStr = opsForList.leftPop(RedisKeys.AI_REVIEW);
            AiReviewJobDTO job = JSONUtil.toBean(jsonStr, AiReviewJobDTO.class);

            String reviewResult = vodService.getReviewResult(job.getJobId());
            if (StrUtil.equals(reviewResult, AliyunAiJobStatusConstant.JOB_FAIL)) {
                log.error("视频：{} AI审核失败！", job.getVideoId());
                continue;
            }
            if (StrUtil.equals(reviewResult, AliyunAiJobStatusConstant.PASS) ||
                    StrUtil.equals(reviewResult, AliyunAiJobStatusConstant.REVIEW)) {
                log.info("视频：{} AI 审核成功", job.getVideoId());
                GetVideoInfoResponse.Video videoInfo = vodService.getVideoInfo(job.getAliyunVideoId());
                videoService.lambdaUpdate()
                        .eq(VideoEntity::getId, job.getVideoId())
                        .set(VideoEntity::getStatus, VideoStatusEnum.PUBLISHED)
                        .set(VideoEntity::getDuration, videoInfo.getDuration())
                        .update();
                redisTemplate.delete(RedisKeys.VIDEO_DETAIL + job.getVideoId());
                userSubscriptionService.publishVideo(job.getVideoId());
                videoService.saveToEsById(job.getVideoId());
            } else if (StrUtil.equals(reviewResult, AliyunAiJobStatusConstant.BLOCK)) {
                log.info("视频：{} AI 审核违规", job.getVideoId());
                videoService.lambdaUpdate()
                        .eq(VideoEntity::getId, job.getVideoId())
                        .set(VideoEntity::getStatus, VideoStatusEnum.BLOCK)
                        .update();
                redisTemplate.delete(RedisKeys.VIDEO_DETAIL + job.getVideoId());
            } else {
                opsForList.rightPush(RedisKeys.AI_REVIEW, jsonStr);
            }
        }
    }
}
