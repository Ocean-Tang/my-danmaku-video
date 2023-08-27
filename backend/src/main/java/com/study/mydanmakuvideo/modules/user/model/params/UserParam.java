package com.study.mydanmakuvideo.modules.user.model.params;

import com.study.mydanmakuvideo.common.enums.GenderEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 用户信息
 *
 * @author Ocean
 * 
 * @apiNote
 */
@Data
@Accessors(chain = true)
public class UserParam {

    @ApiModelProperty("用户ID")
    @NotNull(message = "不能为空", groups = Update.class)
    private Long id;

    @ApiModelProperty("邮箱")
    @NotBlank(message = "不能为空", groups = {Signup.class, Login.class})
    private String email;

    @ApiModelProperty("密码")
    @NotBlank(message = "不能为空", groups = {Signup.class, Login.class})
    private String password;

    @ApiModelProperty("验证码")
    @NotBlank(message = "不能为空", groups = Signup.class)
    @Pattern(regexp = "^\\d{6}$", message = "长度无效，必须纯数字", groups = Signup.class)
    private String captcha;

    @ApiModelProperty("昵称")
    @NotBlank(message = "不能为空", groups = {Signup.class, Update.class})
    private String nick;

    @ApiModelProperty("性别")
    @NotNull(message = "不能为空", groups = Update.class)
    private GenderEnum gender;

    @ApiModelProperty("个性签名")
    private String sign;

    @ApiModelProperty("生日")
    private String birth;

    @ApiModelProperty("头像")
    private MultipartFile avatarFile;


    /**
     * 注册分组
     */
    public interface Signup {
    }

    /**
     * 登录分组
     */
    public interface Login {
    }

    /**
     * 更新分组
     */
    public interface Update {
    }
}
