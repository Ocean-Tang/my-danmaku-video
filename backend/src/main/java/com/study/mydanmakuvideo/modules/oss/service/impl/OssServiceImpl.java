package com.study.mydanmakuvideo.modules.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.SelectObjectRequest;
import com.study.mydanmakuvideo.common.config.AliyunProperty;
import com.study.mydanmakuvideo.modules.oss.service.IOssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * OSS 对象存储服务默认实现类
 *
 * @author huangcanjie
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OssServiceImpl implements IOssService {

    private final OSS aliyunOssClient;
    private final AliyunProperty aliyunProperty;

    @Override
    public String uploadFile(File file) {
        AliyunProperty.OSSProperty ossProperty = aliyunProperty.getOssProperty();
        String key = ossProperty.getDefaultPath() + file.getName();
        PutObjectRequest request = new PutObjectRequest(ossProperty.getBucketName(), key, file);
        aliyunOssClient.putObject(request);
        return ossProperty.getAccessUrlPrefix() + key;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String subFix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        try {
            Path tempFile = Files.createTempFile("tmp", subFix);
            File file = tempFile.toFile();
            multipartFile.transferTo(file);
            return uploadFile(file);
        } catch (IOException e) {
            log.error("创建临时文件失败");
            throw new RuntimeException(e);
        }
    }
}
