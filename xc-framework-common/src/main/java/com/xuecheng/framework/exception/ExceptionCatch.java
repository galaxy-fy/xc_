package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 异常捕获类
 */
@ControllerAdvice//控制器增强
public class ExceptionCatch {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //定义map,配置异常类型所对应的的错误代码
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
    //定义map的build对象, 作用是用来构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();


    //一遇到CustomException就捕获此类异常
    @ExceptionHandler(CustomException.class)//声明捕获哪类异常
    @ResponseBody //回写字符串
    public ResponseResult customException(CustomException customException) {
        //记录日志
        LOGGER.error("catch an Exception{}", customException.getMessage());
        //获得异常数据
        ResultCode resultCode = customException.getResultCode();
        //返回异常数据
        return new ResponseResult(resultCode);
    }

    //一遇到CustomException就捕获此类异常
    @ExceptionHandler(Exception.class)//声明捕获哪类异常
    @ResponseBody //回写字符串
    public ResponseResult exception(Exception exception) {
        //记录日志
        LOGGER.error("catch an Exception{}", exception.getMessage());
        //当EXCEPTIONS为空时,使用build构建一下
        if (EXCEPTIONS == null) {
            EXCEPTIONS = builder.build();//构建成功

        }
        //从EXCEPTIONS中找异常类型所对应的错误代码,如果找到了就把错误代码响应给用户,如果找不到就响应给用户99999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        if (resultCode != null) {
            //找到了
            return new ResponseResult(resultCode);
        } else {
            //返回异常数据
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }
    }

    /**
     * 使用静态代码块当页面一加载来对map赋值
     */
    static {
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
    }
}
