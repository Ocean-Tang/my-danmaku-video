package com.study.mydanmakuvideo;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.KeyPair;

@SpringBootTest
class MyDanmakuVideoApplicationTests {

    public static void main(String[] args) {
        KeyPair rsa =
                SecureUtil.generateKeyPair("RSA");
        System.out.println(Base64.encode(rsa.getPublic().getEncoded()));
        System.out.println(Base64.encode(rsa.getPrivate().getEncoded()));
    }

    @Test
    void contextLoads() {

    }

}
