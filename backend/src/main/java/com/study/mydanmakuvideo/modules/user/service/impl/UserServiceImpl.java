package com.study.mydanmakuvideo.modules.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.common.config.RsaProperty;
import com.study.mydanmakuvideo.common.constant.RedisKeys;
import com.study.mydanmakuvideo.common.dto.Token;
import com.study.mydanmakuvideo.common.enums.ReturnCodeEnums;
import com.study.mydanmakuvideo.common.exception.ApiException;
import com.study.mydanmakuvideo.common.utils.JwtUtil;
import com.study.mydanmakuvideo.modules.oss.service.IOssService;
import com.study.mydanmakuvideo.modules.user.mapper.UserMapper;
import com.study.mydanmakuvideo.modules.user.model.dto.UserDTO;
import com.study.mydanmakuvideo.modules.user.model.entity.UserEntity;
import com.study.mydanmakuvideo.modules.user.model.params.UserParam;
import com.study.mydanmakuvideo.modules.user.service.IRefreshTokenService;
import com.study.mydanmakuvideo.modules.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ocean
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final RsaProperty rsaProperty;
    private final IRefreshTokenService refreshTokenService;
    @Value("${spring.mail.username}")
    private String SERVICE_EMAIL;
    private final IOssService ossService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signup(UserParam user) {
        // 检查是否存在账号
        user.setEmail(rsaProperty.decryptByPrivateKey(user.getEmail()));
        exitsAccount(user.getEmail());

        /* 注册账号 */
        // 初始化实体
        String salt = RandomUtil.randomString(6);
        UserEntity userEntity = Convert.convert(UserEntity.class, user)
                .setSalt(salt)
                .setPassword(SecureUtil.md5(rsaProperty.decryptByPrivateKey(user.getPassword()) + salt))
                .setAvatar("https://edu-hcj.oss-cn-guangzhou.aliyuncs.com/default/default_avatar.png");
        String captcha = redisTemplate.opsForValue().get(RedisKeys.EMAIL_CAPTCHA + userEntity.getEmail());
        if (!StrUtil.equals(captcha, user.getCaptcha())) {
            throw new ApiException(ReturnCodeEnums.CAPTCHA_ERROR);
        }
        this.save(userEntity);
        /* 注册账号 */
    }

    /**
     * 检查是否存在该邮箱账号
     *
     * @param email
     * @return
     */
    private void exitsAccount(String email) {
        if (this.lambdaQuery().eq(UserEntity::getEmail, email).exists()) {
            throw new ApiException(ReturnCodeEnums.EXITS_ACCOUNT);
        }
    }

    @Override
    public void sendCaptcha(String email) {
        String captcha = RandomUtil.randomNumbers(6);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        redisTemplate.opsForValue().set(RedisKeys.EMAIL_CAPTCHA + email, captcha, 1, TimeUnit.MINUTES);
        System.out.println(redisTemplate.opsForValue().get(RedisKeys.EMAIL_CAPTCHA + email));
        mailMessage.setFrom(SERVICE_EMAIL);
        mailMessage.setTo(email);
        mailMessage.setText("请在 1 分钟内使用验证码：" + captcha);
        mailMessage.setSubject("验证码");
        mailSender.send(mailMessage);
    }

    @Override
    public Token login(UserParam user) {
        String email = rsaProperty.decryptByPrivateKey(user.getEmail());
        String password = rsaProperty.decryptByPrivateKey(user.getPassword());

        UserEntity userEntity = getUserByEmail(email);
        String salt = userEntity.getSalt();
        if (!SecureUtil.md5(password + salt).equals(userEntity.getPassword())) {
            throw new ApiException(ReturnCodeEnums.PASSWORD_ERROR);
        }
        Token token = this.generateToken(userEntity.getId());
        refreshTokenService.setToken(userEntity.getId(), token.getRefreshToken());
        return token;
    }

    /**
     * 生成 token 实体
     *
     * @param id
     * @return
     */
    public Token generateToken(Long id) {
        String accessToken = JwtUtil.generateAccessToken(id);
        String refreshToken = JwtUtil.generateRefreshToken(id);
        return new Token(accessToken, refreshToken);
    }

    @Override
    public Token refreshToken(String refreshToken) {

        try {
            JwtUtil.validateExpire(refreshToken);

            Long id = JwtUtil.getIdOfPayload(refreshToken);
            if (!refreshTokenService.exitsRefreshToken(id, refreshToken)) {
                throw new ApiException(ReturnCodeEnums.REFRESH_TOKEN_INVALID);
            }
            Token token = generateToken(id);
            refreshTokenService.setToken(id, token.getRefreshToken());
            return token;
        } catch (ApiException e) {
            throw new ApiException(ReturnCodeEnums.REFRESH_TOKEN_INVALID);
        }
    }


    @Override
    public UserDTO getInfoById(Long userId) {
        return BeanUtil.toBean(this.getById(userId), UserDTO.class);
    }

    @Override
    public void updateInfo(UserParam param) {
        String key = RedisKeys.AVATAR + param.getId();
        Boolean cantUploadAvatar = redisTemplate.hasKey(key);
        MultipartFile avatarFile = param.getAvatarFile();
        if (cantUploadAvatar && ObjectUtil.isNotNull(avatarFile)) {
            throw new ApiException(ReturnCodeEnums.CANT_UPLOAD_AVATAR_TODAY);
        }

        UserEntity entity = BeanUtil.toBean(param, UserEntity.class);
        if (ObjectUtil.isNotNull(avatarFile)) {
            Assert.isTrue(avatarFile.getSize() <= 1024 * 1024 * 5, "上传的头像不能超过 5 M");
            String url = ossService.uploadFile(avatarFile);
            redisTemplate.opsForValue().set(key, "", 1, TimeUnit.DAYS);
            entity.setAvatar(url);
        }

        this.updateById(entity);
    }

    /**
     * 通过邮箱获取用户信息
     *
     * @param email
     * @return
     */
    private UserEntity getUserByEmail(String email) {
        UserEntity user = this.lambdaQuery().eq(UserEntity::getEmail, email)
                .one();

        if (user == null) {
            throw new ApiException(ReturnCodeEnums.NO_USER);
        }
        return user;
    }
}
