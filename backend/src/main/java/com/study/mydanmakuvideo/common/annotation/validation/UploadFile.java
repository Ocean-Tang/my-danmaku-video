package com.study.mydanmakuvideo.common.annotation.validation;

import com.study.mydanmakuvideo.common.validator.UploadFileValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = UploadFileValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
@Documented
public @interface UploadFile {

    String message() default "上传的文件大小不能超过限制";

    /**
     * @return 文件的最大上传大小，单位 M
     */
    int max() default 0;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
