package com.study.mydanmakuvideo.common.config;

import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * @author Ocean
 * 
 */
@ConfigurationProperties(prefix = "aliyun")
@Configuration
@Data
@RequiredArgsConstructor
@Slf4j
public class AliyunProperty {

    private final VodProperty vodProperty;
    private final OSSProperty ossProperty;

    @Bean
    public DefaultAcsClient vodClient() {
        DefaultProfile profile = DefaultProfile
                .getProfile(vodProperty.regionId, vodProperty.accessKeyId, vodProperty.accessKeySecret);
        return new DefaultAcsClient(profile);
    }

    /**
     * 阿里云 OSS 对象存储服务配置
     */
    @ConfigurationProperties(prefix = "aliyun.oss")
    @Configuration
    @Data
    public static class OSSProperty {
        private String endpoint;
        private String bucketName;
        private String accessKeyId;
        private String accessKeySecret;
        private String defaultPath;
        private String accessUrlPrefix;

        public String getAccessUrlPrefix() {
            if (StrUtil.isEmpty(accessUrlPrefix)) {
                String region = endpoint.substring("https://".length());
                this.accessUrlPrefix = "https://" + this.bucketName + "." + region + "/";
            }
            return this.accessUrlPrefix;
        }
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public OSS oss () {
        return new OSSClientBuilder().build(ossProperty.endpoint, ossProperty.accessKeyId, ossProperty.accessKeySecret);
    }

    @ConfigurationProperties(prefix = "aliyun.vod")
    @Configuration
    @Data
    public static class VodProperty {
        private String regionId;
        private String accessKeyId;
        private String accessKeySecret;
        private String aiReviewTemplateId;
    }

}
