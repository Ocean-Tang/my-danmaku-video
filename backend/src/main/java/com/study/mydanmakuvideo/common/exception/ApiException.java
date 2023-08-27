package com.study.mydanmakuvideo.common.exception;

import com.study.mydanmakuvideo.common.enums.ReturnCodeEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ocean
 * 
 * @apiNote 接口调用异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiException extends RuntimeException {

    /**
     * 响应状态码及消息
     */
    private ReturnCodeEnums returnCode;

}
