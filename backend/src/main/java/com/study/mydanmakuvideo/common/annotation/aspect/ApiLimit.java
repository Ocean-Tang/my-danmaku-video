package com.study.mydanmakuvideo.common.annotation.aspect;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流
 *
 * @author huangcanjie
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Component
public @interface ApiLimit {

    /**
     * @return 接口限制次数
     */
    int count() default 20;

    /**
     * @return 接口限流时长
     */
    long time() default 1;

    /**
     * @return 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
