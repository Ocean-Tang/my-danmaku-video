package com.study.mydanmakuvideo.modules.video.controller;

import com.study.mydanmakuvideo.common.annotation.aspect.Login;
import com.study.mydanmakuvideo.common.controller.SuperController;
import com.study.mydanmakuvideo.common.dto.R;
import com.study.mydanmakuvideo.common.utils.JwtUtil;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.service.IVideoHistoryService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 视频历史记录控制器
 *
 * @author huangcanjie
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/video/history")
public class VideoHistoryRestController extends SuperController {

    private final IVideoHistoryService historyService;

    @ApiOperation("保存历史记录")
    @PutMapping("/{videoId}/{time}")
    @Login
    public R updateHistory(@PathVariable Long videoId, @PathVariable Double time) {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        historyService.updateHistory(userId, videoId, time);
        return R.success();
    }

    @ApiOperation("获取当前视频的播放位置")
    @GetMapping("/{videoId}")
    @Login
    public R getVideoPlayPosition(@PathVariable Long videoId) {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        Double time = historyService.getPlayPosition(userId, videoId);
        return R.success(time);
    }

    @ApiOperation("获取用户的历史播放记录")
    @GetMapping
    @Login
    public R getHistory() {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        List<VideoDTO> list = historyService.histories(userId);
        return R.success(list);
    }
}
