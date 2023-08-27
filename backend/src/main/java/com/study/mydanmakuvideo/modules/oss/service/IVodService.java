package com.study.mydanmakuvideo.modules.oss.service;

import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;

import java.io.InputStream;

/**
 * @author huangcanjie
 */
public interface IVodService {

    /**
     * 上传视频
     *
     * @param title       视频标题
     * @param fileName    视频文件名称
     * @param coverUrl    封面地址
     * @param inputStream 文件输入流
     * @return 上传后的阿里云视频ID
     */
    String upload(String title, String fileName, String coverUrl, InputStream inputStream);

    /**
     * 获取视频信息
     *
     * @param videoId 阿里云视频ID
     * @return 阿里云视频信息
     * @throws ClientException 获取视频信息失败异常
     */
    GetVideoInfoResponse.Video getVideoInfo(String videoId) throws ClientException;

    /**
     * 获取视频播放地址
     *
     * @param videoId 阿里云视频ID
     * @return 阿里云视频播放地址
     * @throws ClientException 获取视频播放地址异常
     */
    String getVideoPlayUrl(String videoId) throws ClientException;

    /**
     * 提交AI审核任务
     *
     * @param videoId 阿里云视频ID
     * @return 审核任务ID-JobId
     * @throws ClientException 提交任务异常
     */
    String putAiReview(String videoId) throws ClientException;

    /**
     * 获取审核任务结果
     *
     * @param jobId 阿里云审核任务，任务ID {@link #putAiReview(String)}
     * @return 审核结果，{@link com.study.mydanmakuvideo.common.constant.AliyunAiJobStatusConstant}
     * @throws ClientException 获取审核结果异常
     */
    String getReviewResult(String jobId) throws ClientException;
}
