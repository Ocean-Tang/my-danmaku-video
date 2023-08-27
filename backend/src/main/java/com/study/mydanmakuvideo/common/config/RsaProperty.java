package com.study.mydanmakuvideo.common.config;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.study.mydanmakuvideo.common.enums.ReturnCodeEnums;
import com.study.mydanmakuvideo.common.exception.ApiException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * @author Ocean
 * 
 * @apiNote 非对称加密属性
 */
@Data
@ConfigurationProperties(prefix = "rsa")
@Component
public class RsaProperty {

    private String publicKey;
    private String privateKey;
    private RSA rsa;

    @PostConstruct
    private void initRsaUtil() {
        this.rsa = SecureUtil.rsa(privateKey, publicKey);
    }

    /**
     * 解密内容
     *
     * @param content
     * @return
     */
    public String decryptByPrivateKey(String content) {
        try {
            return this.rsa.decryptStr(content, KeyType.PrivateKey, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ApiException(ReturnCodeEnums.FAIL);
        }
    }

    /**
     * 加密内容
     *
     * @param content
     * @return
     */
    public String encryptByPublicKey(String content) {
        return this.rsa.encryptBase64(content, StandardCharsets.UTF_8, KeyType.PublicKey);
    }
}
