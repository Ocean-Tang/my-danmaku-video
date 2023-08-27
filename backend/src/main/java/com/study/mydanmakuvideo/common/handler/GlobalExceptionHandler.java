package com.study.mydanmakuvideo.common.handler;

import com.study.mydanmakuvideo.common.dto.R;
import com.study.mydanmakuvideo.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ocean
 * 
 * @apiNote
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * @param e 异常对象
     * @return 全局统一异常对象，并且返回异常信息
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R exception(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return R.failure();
    }


    /**
     * 接口异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public R exception(ApiException e, HttpServletRequest request) {
        log.error("调用接口[{}]({})异常，参数：[{}]， 异常消息：[{}]-{}",
                request.getMethod(), request.getRequestURI(),
                request.getQueryString(),
                e.getReturnCode().getCode(), e.getReturnCode().getMsg());
        return R.failure(e.getReturnCode());
    }

    @ExceptionHandler({BindException.class})
    @ResponseBody
    public R handleValidationException(BindException e, HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        StringBuilder returnMsg = new StringBuilder();
        for (ObjectError error : bindingResult.getAllErrors()) {
            FieldError fieldError = (FieldError) error;
            sb.append(fieldError.getField())
                    .append(" =【")
                    .append(fieldError.getRejectedValue())
                    .append("】校验失败：")
                    .append(fieldError.getDefaultMessage())
                    .append("\n");

            returnMsg.append(fieldError.getDefaultMessage()).append("；");
            result.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.error("请求[{}]({})参数校验出错，校验详情：\n{}", request.getMethod(), request.getRequestURL(), sb.toString());
        returnMsg.deleteCharAt(returnMsg.length() - 1);
        return R.failure().setMsg(returnMsg.toString());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public R handleAssertException(IllegalArgumentException e) {

        return R.failure(e.getMessage());
    }


}
