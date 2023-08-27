package com.study.mydanmakuvideo.common.validator;

import com.study.mydanmakuvideo.common.annotation.validation.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 文件上传属性校验
 *
 * @author huangcanjie
 */
public class UploadFileValidator implements ConstraintValidator<UploadFile, MultipartFile> {

    private UploadFile info;

    @Override
    public void initialize(UploadFile constraintAnnotation) {
        this.info = constraintAnnotation;
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value.getSize() >= (long) info.max() * 1024 * 1024) {
            return false;
        }
        return true;
    }

}
