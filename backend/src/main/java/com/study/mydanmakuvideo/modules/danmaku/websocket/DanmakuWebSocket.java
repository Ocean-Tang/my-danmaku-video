package com.study.mydanmakuvideo.modules.danmaku.websocket;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.study.mydanmakuvideo.common.config.MQConfig;
import com.study.mydanmakuvideo.modules.danmaku.model.dto.DplayerDanmakuDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoEntity;
import com.study.mydanmakuvideo.modules.video.service.IVideoService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ServerEndpoint("/danmaku/{videoId}")
@Component
@Slf4j
public class DanmakuWebSocket {

    private Session session;
    private String sessionId;

    @Getter
    private static final ConcurrentHashMap<Long, VideoSessionInfo> videoSessions = new ConcurrentHashMap<>();
    private static IVideoService videoService;
    private static RedisTemplate<String, String> redisTemplate;
    private static RabbitTemplate rabbitTemplate;
    private Long videoId;

    @Autowired
    public void setVideoService(IVideoService videoService) {
        DanmakuWebSocket.videoService = videoService;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        DanmakuWebSocket.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        DanmakuWebSocket.rabbitTemplate = rabbitTemplate;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("videoId") Long videoId) throws IOException {
        VideoEntity byId = videoService.getById(videoId);
        long viewers = putVideoSessionInfo(session, videoId);

        sendViewers(videoId);

        log.info("连接成功, 当前视频：{}，观看人数：{}", videoId, viewers);
    }

    private void sendViewers(Long videoId) throws IOException {
        DplayerDanmakuDTO danmaku = new DplayerDanmakuDTO();
        danmaku.setViewers(getViewers(videoId)).setPlayer(videoId);
        rabbitTemplate.convertAndSend(MQConfig.DANAMKU_EXCHANGE, "", JSON.toJSONString(danmaku));
    }

    private long putVideoSessionInfo(Session session, Long videoId) {
        this.session = session;
        this.sessionId = session.getId();
        this.videoId = videoId;
        VideoSessionInfo videoSessionInfo = videoSessions.getOrDefault(videoId, new VideoSessionInfo());
        videoSessionInfo.addSession(sessionId, session);
        videoSessions.put(videoId, videoSessionInfo);
        return videoSessionInfo.getViewers();
    }

    @OnClose
    public void onClose(@PathParam("videoId") Long videoId) throws IOException {
        VideoSessionInfo videoSessionInfo = videoSessions.get(videoId);
        videoSessionInfo.removeSession(sessionId);
        log.info("视频：{}，用户断开连接，观看人数：{}", videoId, videoSessionInfo.getViewers());
        if (!videoSessionInfo.hasViewers()) {
            videoSessions.remove(videoId);
            log.info("当前视频：{}，已无人观看", videoId);
        }
        sendViewers(videoId);
    }

    /**
     * 接受到消息后，转发到各个会话
     * 第一次压测：平均吞吐量 ：238/s
     *
     * @param videoId
     * @param message
     * @throws IOException
     */
    @OnMessage
    public void onMessage(@PathParam("videoId") Long videoId, String message) throws IOException {
        DplayerDanmakuDTO danmaku = JSONUtil.toBean(message, DplayerDanmakuDTO.class);
        danmaku.setSessionId(this.sessionId);
        danmaku.setViewers(getViewers(videoId));

        log.info("视频：{} 接受到弹幕：{}", videoId, message);

        // 转发给消息队列
        rabbitTemplate.convertAndSend(MQConfig.DANAMKU_EXCHANGE, "", JSON.toJSONString(danmaku));

        // 缓存到 Redis
        redisTemplate.opsForList().rightPush("danmaku:" + videoId, JSONUtil.toJsonStr(danmaku.toArrayInfo()));
        redisTemplate.opsForList().rightPush("danmaku:new:" + videoId, message);

    }

    @OnError
    public void onError(@PathParam("videoId") Long videoId, Session session, Throwable error) {
        log.error("视频 {} 会话发生错误", videoId, error);
        if (videoSessions.containsKey(videoId)) {
            VideoSessionInfo videoSessionInfo = videoSessions.get(videoId);
            videoSessionInfo.removeSession(session.getId());
        }
    }

    public long getViewers(Long videoId) {
        VideoSessionInfo videoSessionInfo = videoSessions.get(videoId);
        if (videoSessionInfo != null) {
            return videoSessionInfo.getViewers();
        }
        return 0;
    }

    public static class VideoSessionInfo {

        private final AtomicLong viewer = new AtomicLong(0);
        private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

        public long getViewers() {
            return viewer.get();
        }

        public void addSession(String sessionId, Session session) {
            if (!sessions.containsKey(sessionId)) {
                sessions.put(sessionId, session);
                viewer.incrementAndGet();
            }
        }

        public void removeSession(String sessionId) {
            if (sessions.containsKey(sessionId)) {
                sessions.remove(sessionId);
                viewer.decrementAndGet();
            }
        }

        public Session getSession(String sessionId) {
            return sessions.get(sessionId);
        }

        public Collection<Session> getAllSession() {
            return sessions.values();
        }

        public boolean hasViewers() {
            return !sessions.isEmpty();
        }
    }
}
