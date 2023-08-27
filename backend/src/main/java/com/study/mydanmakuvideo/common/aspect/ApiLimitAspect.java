package com.study.mydanmakuvideo.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.study.mydanmakuvideo.common.annotation.aspect.ApiLimit;
import com.study.mydanmakuvideo.common.enums.ReturnCodeEnums;
import com.study.mydanmakuvideo.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 接口限流切面类
 *
 * @author huangcanjie
 */
@Aspect
@Component
@RequiredArgsConstructor
@Order(0)
@Slf4j
public class ApiLimitAspect {

    private final HttpServletRequest request;
    private final RedisTemplate<String, String> redisTemplate;

    @Before("@within(com.study.mydanmakuvideo.common.annotation.aspect.ApiLimit)" +
            " || @annotation(com.study.mydanmakuvideo.common.annotation.aspect.ApiLimit) ")
    public void limitApi(JoinPoint joinPoint) {
        String userAgent = request.getHeader("User-Agent");
        if (StrUtil.isBlank(userAgent)) {
            throw new ApiException(ReturnCodeEnums.FAIL);
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = method.getDeclaringClass();

        ApiLimit apiLimit = method.getAnnotation(ApiLimit.class);
        apiLimit = apiLimit == null ? clazz.getAnnotation(ApiLimit.class) : apiLimit;

        String name = joinPoint.getSignature().getName();
        String key = userAgent + ":" + name;
        redisTemplate.opsForValue().increment(key);
        String value = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotEmpty(value)) {
            Integer count = Integer.valueOf(value);
            if (count >= apiLimit.count()) {
                log.error("{} 被限流接口：{}", userAgent, name);
                throw new ApiException(ReturnCodeEnums.LIMIT_ACCESS);
            }
        }
        redisTemplate.expire(key, apiLimit.time(), apiLimit.timeUnit());
    }
}
