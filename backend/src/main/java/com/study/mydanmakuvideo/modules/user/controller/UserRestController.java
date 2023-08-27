package com.study.mydanmakuvideo.modules.user.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.study.mydanmakuvideo.common.annotation.aspect.Login;
import com.study.mydanmakuvideo.common.config.RsaProperty;
import com.study.mydanmakuvideo.common.controller.SuperController;
import com.study.mydanmakuvideo.common.dto.R;
import com.study.mydanmakuvideo.common.dto.Token;
import com.study.mydanmakuvideo.common.enums.ReturnCodeEnums;
import com.study.mydanmakuvideo.common.utils.JwtUtil;
import com.study.mydanmakuvideo.modules.user.model.dto.UserDTO;
import com.study.mydanmakuvideo.modules.user.model.params.UserParam;
import com.study.mydanmakuvideo.modules.user.service.IRefreshTokenService;
import com.study.mydanmakuvideo.modules.user.service.IUserService;
import com.study.mydanmakuvideo.modules.user.service.IUserSubscriptionService;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户模块
 *
 * @author ocean
 * 
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController extends SuperController {

    private final RsaProperty rsaProperty;
    private final IUserService userService;
    private final IRefreshTokenService refreshTokenService;
    private final IUserSubscriptionService userSubscriptionService;


    /**
     * 获取公钥
     *
     * @return
     */
    @GetMapping("/rsa-pks")
    public R getPublicKey() {
        return R.success(rsaProperty.getPublicKey());
    }

    /**
     * 加密内容 - 测试用
     *
     * @param str 待加密内容
     * @return
     */
    @GetMapping("/encrypt")
    public R encrypt(String str) {
        return R.success(rsaProperty.encryptByPublicKey(str));
    }

    /**
     * 解密内容 - 测试用
     *
     * @param str 待解密内容
     * @return
     */
    @PostMapping("/decrypt")
    public R decrypt(@RequestBody String str) {
        return R.success(rsaProperty.decryptByPrivateKey(str));
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @PostMapping("/signup")
    public R signup(@RequestBody @Validated(UserParam.Signup.class) UserParam user) {
        userService.signup(user);
        return R.success();
    }

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody @Validated(UserParam.Login.class) UserParam user) {
        Token token = userService.login(user);
        return R.success(token);
    }

    @GetMapping("/info")
    @Login
    @ApiOperation("查看个人信息")
    public R getInfo() {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        if (userId == null) {
            return R.failure(ReturnCodeEnums.NO_LOGIN);
        }
        UserDTO userDTO = userService.getInfoById(userId);
        return this.success(userDTO);
    }

    @GetMapping("/info/{userId}")
    @ApiOperation("查看用户资料")
    public R getUserInfo(@PathVariable Long userId) {
        UserDTO userDTO = userService.getInfoById(userId);
        return this.success(userDTO);
    }

    @ApiOperation("更新个人信息")
    @PutMapping("/info")
    @Login
    public R updateInfo(@ModelAttribute UserParam param) {
        userService.updateInfo(param);
        return success();
    }

    /**
     * 邮箱获取验证码
     *
     * @param email
     * @return
     */
    @PostMapping("/mail-captcha")
    public R captcha(String email) {
        userService.sendCaptcha(email);
        return R.success();
    }

    /**
     * 刷新token
     *
     * @param refreshToken
     * @return
     */
    @PutMapping("/refresh-token")
    public R updateToken(@RequestParam String refreshToken) {
        Token token = userService.refreshToken(refreshToken);
        return success(token);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @DeleteMapping("/logout")
    public R logout(@RequestHeader("accessToken") String accessToken) {
        refreshTokenService.logout(accessToken);
        return success();
    }

    @ApiOperation("订阅用户")
    @Login
    @PostMapping("/subscribe/{userId}")
    public R subscribe(@PathVariable Long userId) {
        Long currentUserId = JwtUtil.LOGIN_USER_HANDLER.get();
        Assert.isFalse(NumberUtil.equals(userId, currentUserId), "不能关注自己！");
        userSubscriptionService.subscribe(currentUserId, userId);
        return R.success();
    }

    @ApiOperation("取消订阅用户")
    @Login
    @DeleteMapping("/subscribe/{userId}")
    public R cancelSubscribe(@PathVariable Long userId) {
        Long currentUserId = JwtUtil.LOGIN_USER_HANDLER.get();
        userSubscriptionService.cancelSubscribe(currentUserId, userId);
        return R.success();
    }

    @ApiOperation("获取新的订阅消息")
    @Login
    @GetMapping("/subscribe/msg")
    public R getVideoMsg() {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        List<VideoDTO> msg = userSubscriptionService.getMsg(userId);
        return success(msg);
    }

    @ApiOperation("消费指定的消息")
    @DeleteMapping("/subscribe/msg/{index}")
    @Login
    public R consumeMsg(@PathVariable Integer index) {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        userSubscriptionService.consumeMsg(userId, index);
        return success();
    }

    @ApiOperation("是否订阅该用户")
    @GetMapping("/is-subscription/{userId}")
    @Login
    public R isSubscription(@PathVariable Long userId) {
        Long currentUserId = JwtUtil.LOGIN_USER_HANDLER.get();
        return success(userSubscriptionService.isSubscription(currentUserId, userId));
    }

    @ApiOperation("获取当前用户的关注列表")
    @GetMapping("/subscription")
    @Login
    public R getSubscriptions() {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        List<UserDTO> users = userSubscriptionService.getSubscriptions(userId);
        return success(users);
    }

    @ApiOperation("获取当前用户的被关注列表")
    @GetMapping("/subscribed")
    @Login
    public R getSubscribedList() {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        List<UserDTO> users = userSubscriptionService.getSubscribedList(userId);
        return success(users);
    }

}
