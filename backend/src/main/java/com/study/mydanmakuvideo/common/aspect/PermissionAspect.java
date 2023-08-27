package com.study.mydanmakuvideo.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.study.mydanmakuvideo.common.annotation.aspect.Login;
import com.study.mydanmakuvideo.common.enums.ReturnCodeEnums;
import com.study.mydanmakuvideo.common.exception.ApiException;
import com.study.mydanmakuvideo.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录鉴权切面
 *
 * @author Ocean
 * 
 */
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final HttpServletRequest request;

    @Pointcut("@annotation(com.study.mydanmakuvideo.common.annotation.aspect.Login)")
    public void check() {
    }

    @Before("check() && @annotation(login)")
    public void before(JoinPoint joinPoint, Login login) {
        String accessToken = request.getHeader("accessToken");
        if (StrUtil.isBlank(accessToken)) {
            throw new ApiException(ReturnCodeEnums.NO_LOGIN);
        }
        JwtUtil.validateExpire(accessToken);
    }

}
