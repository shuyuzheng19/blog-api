package com.zsy.blog.common;

import com.zsy.blog.response.Result;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 郑书宇
 * @create 2023/1/16 17:13
 * @desc
 */
@RestControllerAdvice
public class GlobalExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map fail(HttpServletRequest request, MethodArgumentNotValidException exception){
        BindingResult bindingResult=exception.getBindingResult();
        Map<String,Object> map=new HashMap();
        map.put("code",ResultCode.PARAMS_ERROR.value());
        map.put("message",bindingResult.getFieldError().getDefaultMessage());
        map.put("error_url",request.getRequestURL());
        return map;
    }

    @ExceptionHandler(GlobalException.class)
    public Result fail2(GlobalException exception){
        return Result.customize(exception.getCode(),exception.getMessage());
    }

}
