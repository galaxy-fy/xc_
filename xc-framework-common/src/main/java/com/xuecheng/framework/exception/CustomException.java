package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 自定义异常类
 */
public class CustomException extends RuntimeException {
    //错误代码
    private ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        //异常信息为错误代码+异常信息
        this.resultCode = resultCode;
    }

    /**
     * 通过构造方法注入resultCode
     * @return
     */
    public ResultCode getResultCode() {
        return this.resultCode;
    }
}
