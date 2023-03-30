package com.wang.elema_take_out.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 * @author wjh
 * @create 2023-03-16
 */
@Slf4j
//响应的数据是R<String>，是json数据，所有用@ResponseBody
@ResponseBody
//@ControllerAdvice表示这个类是aop切面，annotations表示作用在加了什么注解的类上
@ControllerAdvice(annotations = {Controller.class, RestController.class})
public class GlobalExceptionHandler {

//  @ExceptionHandler表示如果报了指定的异常就会执行这个方法
//    java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'zhangsan' for key 'employee.idx_username'
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler (SQLIntegrityConstraintViolationException ex){
//        打印异常信息
        log.error(ex.getMessage());

//        如果这个异常包含这个关键字
        if (ex.getMessage().contains("Duplicate entry")){
//            将异常信息用空格隔开，这样一来'zhangsan'就在数据的下标为2的位置
            String[] split = ex.getMessage().split(" ");
            return R.error(split[2]+"已存在");
        }
        return R.error("未知错误！");
    }


    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler (CustomException ex){
//        打印异常信息
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }

}
