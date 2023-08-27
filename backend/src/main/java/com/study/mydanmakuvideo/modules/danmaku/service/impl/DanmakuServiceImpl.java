package com.study.mydanmakuvideo.modules.danmaku.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.common.constant.RedisKeys;
import com.study.mydanmakuvideo.modules.danmaku.mapper.DanmakuMapper;
import com.study.mydanmakuvideo.modules.danmaku.model.entity.DanmakuEntity;
import com.study.mydanmakuvideo.modules.danmaku.service.IDanmakuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 弹幕表 服务实现类
 * </p>
 *
 * @author ocean
 * 
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DanmakuServiceImpl extends ServiceImpl<DanmakuMapper, DanmakuEntity> implements IDanmakuService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public List<Object[]> parseToArrayInfo(List<String> range) {
        return range.stream().map(i -> JSONUtil.parseArray(i).toArray()).collect(Collectors.toList());
    }

    private List<Object[]> parseDanmakuEntityToArrayInfo(List<DanmakuEntity> danmakuEntities) {
        return danmakuEntities.stream().map(DanmakuEntity::toArrayInfo).collect(Collectors.toList());
    }

    @Override
    public List<Object[]> getDanmakusAsArrayInfoByVideoId(Long videoId) {
        String key = RedisKeys.DANMAKU + videoId;
        List<String> range = redisTemplate.opsForList().range(key, 0, -1);
        if (CollectionUtil.isNotEmpty(range)) {
            // 又被获取，重新设置过期时间
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
            return this.parseToArrayInfo(range);
        }

        List<DanmakuEntity> danmakuEntities = CollectionUtil.emptyIfNull(this.lambdaQuery().eq(DanmakuEntity::getVideoId, videoId).list());
        List<Object[]> danmakus = parseDanmakuEntityToArrayInfo(danmakuEntities);
        if (CollectionUtil.isNotEmpty(danmakus)) {
            redisTemplate.opsForList()
                    .rightPushAll(key, danmakus.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList()));
        }
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        return danmakus;
    }

    @Override
    public long countByVideoId(Long videoId) {
        Long count = this.lambdaQuery().eq(DanmakuEntity::getVideoId, videoId).count();
        Long size = redisTemplate.opsForList().size(RedisKeys.DANMAKU_NEW + videoId);
        return count + size;
    }

}
