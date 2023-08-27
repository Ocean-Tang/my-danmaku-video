package com.study.mydanmakuvideo.common.dto;

import com.study.mydanmakuvideo.common.enums.ReturnCodeEnums;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;

/**
 * @author Ocean
 * 
 * @apiNote 统一返回结果
 */
@Data
@Accessors(chain = true)
public class R {
    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private Object data;

    private R () {
        data = new HashMap<>();
    }

    public static R success() {
        return result(ReturnCodeEnums.SUCCESS);
    }

    public static R success(Object data) {
        return success().setData(data);
    }

    public static R failure() {
        return result(ReturnCodeEnums.FAIL);
    }

    public static R failure(ReturnCodeEnums returnCode) {
        return result(returnCode);
    }

    public static R failure(String msg) {
        return new R().setCode(400000)
                .setMsg(msg);
    }

    private static R result(ReturnCodeEnums returnCode) {
        return new R().setCode(returnCode.getCode())
                .setMsg(returnCode.getMsg());
    }

}
