package com.study.mydanmakuvideo.common.constant;

/**
 * 阿里云AI审核任务状态
 *
 * @author huangcanjie
 */
public class AliyunAiJobStatusConstant {
    public final static String JOB_SUCCESS = "success";
    public final static String JOB_INIT = "init";
    public final static String JOB_FAIL = "fail";
    public final static String JOB_PROCESSING = "processing";

    // 通过
    public static final String PASS = "pass";
    // 疑似
    public static final String REVIEW = "review";
    // 违规
    public static final String BLOCK = "block";
}
