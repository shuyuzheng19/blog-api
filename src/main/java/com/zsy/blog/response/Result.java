package com.zsy.blog.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zsy.blog.common.ResultCode;
import lombok.Data;

/**
 * @author 郑书宇
 * @create 2023/1/16 14:58
 * @desc 全局接口返回
 */
@Data @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class Result {

    //默认成功
    private final static Builder DEFAULT_SUCCESS=new Builder().code(ResultCode.SUCCESS.value()).message("成功");
    //默认失败
    private final static Result DEFAULT_FAILURE=new Builder().code(ResultCode.FAIL.value()).message("失败").build();
    //默认服务器异常
    private final static Result DEFAULT_ERROR=new Builder().code(ResultCode.ERROR.value()).message("服务器异常").build();


    //返回状态码
    private int code;

    //返回消息
    private String message;

    //返回数据
    private Object data;

    //成功返回
    public static Result ok(){
        return DEFAULT_SUCCESS.build();
    }

    //成功且带数据的返回
    public static Result ok(Object data){
        return DEFAULT_SUCCESS.data(data).build();
    }

    //失败返回
    public static Result fail(){
        return DEFAULT_FAILURE;
    }

    //服务器异常返回
    public static Result error(){
        return DEFAULT_ERROR;
    }

    //自定义返回
    public static Result customize(int code,String message){
        return new Builder().code(code).message(message).build();
    }

    //自定义返回
    public static Result customize(int code,String message,Object data){
        return new Builder().code(code).message(message).data(data).build();
    }


    private Result(){

    }

    private Result(Builder builder){
        this.code=builder.code;
        this.message=builder.message;
        this.data=builder.data;
    }


    public static class Builder{
        private int code;

        private String message;

        private Object data;

        public Builder code(int code){
            this.code=code;
            return this;
        }

        public Builder message(String message){
            this.message=message;
            return this;
        }

        public Builder data(Object data){
            this.data=data;
            return this;
        }

        public Result build(){
            return new Result(this);
        }
    }

}
