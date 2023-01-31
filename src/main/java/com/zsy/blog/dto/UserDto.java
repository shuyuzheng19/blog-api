package com.zsy.blog.dto;

import com.zsy.blog.entitys.User;
import com.zsy.blog.enums.SexEnum;
import com.zsy.blog.enums.UserStatusEnum;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author 郑书宇
 * @create 2023/1/16 16:37
 * @desc
 */
@Data
public class UserDto {

    @NotEmpty(message="用户账号不能为空")
    @Size(min = 8,max = 16,message = "账号不能小于8个字符并且不能大于16个字符")
    private String username;

    @NotEmpty(message="邮箱验证码不能为空")
    private String code;

    @NotEmpty(message="密码不能为空")
    @Size(min = 8,max = 16,message = "密码不能小于8个字符并且不能大于16个字符")
    private String password;

    @NotEmpty(message="用户名称不能为空")
    @Size(min = 3,max = 16,message = "用户名不能小于3个字符并且不能大于16个字符")
    private String nickName;

    @NotEmpty(message="邮箱不能为空!")
    @Email(message = "邮箱校验失败,请输入正确格式的邮箱")
    private String email;

    @NotEmpty(message="用户图标不能为空")
    private String icon;

    private int sex=0;

    public User toUserDo(){
        if(this==null){
            return null;
        }

        User user=new User();
        user.setId(null);
        Date now=new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setIcon(this.icon);
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setEmail(this.email);
        user.setNickName(this.nickName);
        user.setSex(this.sex==0? SexEnum.MAN : SexEnum.WOMEN);
        user.setStatus(UserStatusEnum.NORMA);
        return user;
    }
}
