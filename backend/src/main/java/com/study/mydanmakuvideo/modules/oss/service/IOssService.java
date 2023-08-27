package com.study.mydanmakuvideo.modules.oss.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * OSS 对象存储服务
 */
public interface IOssService {

    /**
     * 上传文件并返回 图片 url
     * @param file      上传文件
     * @return          url
     */
    String uploadFile (File file);

    /**
     * 上传文件并返回 图片 url
     * @param multipartFile      上传文件
     * @return          url
     */
    String uploadFile(MultipartFile multipartFile);

}
