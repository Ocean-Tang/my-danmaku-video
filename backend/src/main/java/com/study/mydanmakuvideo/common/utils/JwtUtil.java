package com.study.mydanmakuvideo.common.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.study.mydanmakuvideo.common.enums.ReturnCodeEnums;
import com.study.mydanmakuvideo.common.exception.ApiException;

import java.util.Date;

/**
 * @author Ocean
 * 
 * @apiNote JwtUtil 工具类
 */
public class JwtUtil {

    public static ThreadLocal<Long> LOGIN_USER_HANDLER = new ThreadLocal<>();

    /**
     * token 过期时间
     */
    private final static long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 60 * 2;
    /**
     * refresh token 过期时间
     */
    private final static long REFERENCE_TOKEN_EXPIRE = 1000 * 60 * 60 * 24 * 2;

    /**
     * 密钥
     */
    private final static String key = "zh7wgpprlekx0xfy";

    public static String generateAccessToken(Object payload) {
        return generateToken(payload, ACCESS_TOKEN_EXPIRE);
    }

    public static String generateRefreshToken(Object payload) {
        return generateToken(payload, REFERENCE_TOKEN_EXPIRE);
    }

    private static String generateToken(Object payload, long expiration) {
        return JWT.create().setSigner(JWTSignerUtil.createSigner("HS256", key.getBytes()))
                .setPayload("id", payload)
                .setIssuedAt(new Date())
                .setExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .setHeader("typ", "JWT")
                .setHeader("alg", "HS256")
                .sign();
    }

    /**
     * 验证token 是否过期
     *
     * @param accessToken
     * @throws Exception
     */
    public static void validateExpire(String accessToken) {
        try {
            JWTValidator.of(accessToken).validateDate();
        } catch (Exception e) {
            throw new ApiException(ReturnCodeEnums.TOKEN_INVALID);
        }
    }

    public static Long getIdOfPayload(String token) {
        return (Long) JWTUtil.parseToken(token).getPayload("id");
    }
}
