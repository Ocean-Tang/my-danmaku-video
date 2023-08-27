package com.study.mydanmakuvideo.modules.danmaku.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.mydanmakuvideo.modules.danmaku.model.entity.DanmakuEntity;

import java.util.List;

/**
 * <p>
 * 弹幕表 服务类
 * </p>
 *
 * @author ocean
 * 
 */
public interface IDanmakuService extends IService<DanmakuEntity> {

    List<Object[]> parseToArrayInfo(List<String> range);

    List<Object[]> getDanmakusAsArrayInfoByVideoId(Long videoId);

    long countByVideoId(Long videoId);
}
