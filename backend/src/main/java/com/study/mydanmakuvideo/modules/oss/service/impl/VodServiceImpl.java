package com.study.mydanmakuvideo.modules.oss.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.GetAIMediaAuditJobRequest;
import com.aliyuncs.vod.model.v20170321.GetAIMediaAuditJobResponse;
import com.aliyuncs.vod.model.v20170321.GetMediaAuditResultRequest;
import com.aliyuncs.vod.model.v20170321.GetMediaAuditResultResponse;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;
import com.aliyuncs.vod.model.v20170321.SubmitAIMediaAuditJobRequest;
import com.aliyuncs.vod.model.v20170321.SubmitAIMediaAuditJobResponse;
import com.study.mydanmakuvideo.common.config.AliyunProperty;
import com.study.mydanmakuvideo.common.constant.AliyunAiJobStatusConstant;
import com.study.mydanmakuvideo.common.enums.ReturnCodeEnums;
import com.study.mydanmakuvideo.common.exception.ApiException;
import com.study.mydanmakuvideo.modules.oss.service.IVodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * 阿里云视频点播服务类实现
 *
 * @author huangcanjie
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VodServiceImpl implements IVodService {

    private final DefaultAcsClient acsClient;
    private final AliyunProperty aliyunProperty;

    @Override
    public String upload(String title, String fileName, String coverUrl, InputStream inputStream) {
        AliyunProperty.VodProperty vodProperty = aliyunProperty.getVodProperty();
        UploadStreamRequest request = new UploadStreamRequest(vodProperty.getAccessKeyId(),
                vodProperty.getAccessKeySecret(), title, fileName, inputStream);
        request.setCoverURL(coverUrl);
        request.setPrintProgress(false);
        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadStreamResponse response = uploader.uploadStream(request);
        System.out.print("RequestId=" + response.getRequestId() + "\n");  //请求视频点播服务的请求ID
        if (response.isSuccess()) {
            return response.getVideoId();
        } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            log.error("上传视频失败，VideoId={},ErrorCode={},ErrorMessage={}", response.getVideoId(), response.getCode(), response.getMessage());
            throw new ApiException(ReturnCodeEnums.UPLOAD_VIDEO_ERROR);
        }
    }

    @Override
    public GetVideoInfoResponse.Video getVideoInfo(String videoId) throws ClientException {
        GetVideoInfoRequest request = new GetVideoInfoRequest();
        request.setVideoId(videoId);
        GetVideoInfoResponse response = acsClient.getAcsResponse(request);
        Assert.notNull(response.getVideo(), "获取不到视频信息，videoId：{}", videoId);
        return response.getVideo();
    }


    @Override
    public String getVideoPlayUrl(String videoId) throws ClientException {
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        request.setVideoId(videoId);
        GetPlayInfoResponse response = acsClient.getAcsResponse(request);
        return response.getPlayInfoList().get(0).getPlayURL();
    }

    @Override
    public String putAiReview(String videoId) throws ClientException {
        SubmitAIMediaAuditJobRequest request = new SubmitAIMediaAuditJobRequest();
        request.setMediaId(videoId);
        request.setTemplateId(aliyunProperty.getVodProperty().getAiReviewTemplateId());
        SubmitAIMediaAuditJobResponse response = acsClient.getAcsResponse(request);
        return response.getJobId();
    }

    @Override
    public String getReviewResult(String jobId) throws ClientException {
        GetAIMediaAuditJobRequest jobRequest = new GetAIMediaAuditJobRequest();
        jobRequest.setJobId(jobId);
        GetAIMediaAuditJobResponse jobResponse = acsClient.getAcsResponse(jobRequest);
        String jobStatus = jobResponse.getMediaAuditJob().getStatus();
        if (!StrUtil.equals(jobStatus, AliyunAiJobStatusConstant.JOB_SUCCESS)) {
            return jobStatus;
        }

        GetMediaAuditResultRequest request = new GetMediaAuditResultRequest();
        request.setMediaId(jobResponse.getMediaAuditJob().getMediaId());
        GetMediaAuditResultResponse response = acsClient.getAcsResponse(request);
        return response.getMediaAuditResult().getSuggestion();
    }
}
