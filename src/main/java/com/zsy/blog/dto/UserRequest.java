package com.zsy.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author 郑书宇
 * @create 2023/1/16 23:57
 * @desc
 */
@Data
public class UserRequest {
    @NotEmpty(message = "账号不能为空")
    private String username;
    @NotEmpty(message = "密码不能为空")
    private String password;
    @NotEmpty(message = "验证码不能为空")
    private String code;
}
