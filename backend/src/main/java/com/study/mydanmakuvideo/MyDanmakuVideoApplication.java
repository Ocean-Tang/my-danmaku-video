package com.study.mydanmakuvideo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyDanmakuVideoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyDanmakuVideoApplication.class, args);
    }

}
