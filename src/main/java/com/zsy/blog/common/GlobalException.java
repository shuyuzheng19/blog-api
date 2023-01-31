package com.zsy.blog.common;

/**
 * @author 郑书宇
 * @create 2023/1/16 19:38
 * @desc
 */
public class GlobalException extends RuntimeException{

    private int code;

    private String message;

    public GlobalException(int code,String message){
        super(message);
        this.code=code;
        this.message=message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
