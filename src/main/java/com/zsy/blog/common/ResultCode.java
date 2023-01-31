package com.zsy.blog.common;

/**
 * @author 郑书宇
 * @create 2023/1/16 15:03
 * @desc
 */
public enum ResultCode {

    SUCCESS(200),//成功
    FAIL(10001),//失败
    ERROR(500),//服务器异常
    NOT_FOUNT(404),//找不到
    PARAMS_ERROR(10001),//参数错误
    AUTHENTICATE_ERROR(10002),//认证错误
    AUTHORIZE_ERROR(10003),//授权错误
    FILE_SIZE_ERROR(10004),//文件大小超出
    FILE_TYPE_ERROR(10005),//文件类型错误
    FILE_UPLOAD_ERROR(10006),//文件上传失败
    STR_EMPTY_ERROR(10007),//字符串为空错误
    MATCH_ERROR(10008),//规则匹配错误
    IP_ERROR(20000),//IP被封禁
    ;

    private int code;

    ResultCode(int code){
        this.code=code;
    }

    public int value() {
        return code;
    }
}
