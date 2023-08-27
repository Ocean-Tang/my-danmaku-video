package com.study.mydanmakuvideo.modules.danmaku.mq;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.study.mydanmakuvideo.common.config.MQConfig;
import com.study.mydanmakuvideo.modules.danmaku.model.dto.DplayerDanmakuDTO;
import com.study.mydanmakuvideo.modules.danmaku.websocket.DanmakuWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Collection;

/**
 * 弹幕消息队列消费者
 *
 * @author huangcanjie
 */
@Component
@Slf4j
public class DanmakuReceiver {

    @RabbitListener(queues = MQConfig.DANAMKU_QUEUE)
    public void receive(String message) throws IOException {
        log.info("{} 消费者开始消费消息", MQConfig.DANAMKU_QUEUE);
        DplayerDanmakuDTO dto = JSONUtil.toBean(message, DplayerDanmakuDTO.class);
        DanmakuWebSocket.VideoSessionInfo videoSessionInfo = DanmakuWebSocket.getVideoSessions().get(dto.getPlayer());
        if (videoSessionInfo == null) {
            return;
        }
        Collection<Session> sessions = videoSessionInfo.getAllSession();

        for (Session target : sessions) {
            if (target.isOpen() && !StrUtil.equals(target.getId(), dto.getSessionId())) {
                target.getBasicRemote().sendText(message);
            }
        }
    }
}
