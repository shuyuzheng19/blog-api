package com.zsy.blog.controllers;

import com.zsy.blog.common.Constants;
import com.zsy.blog.common.GlobalException;
import com.zsy.blog.entitys.User;
import com.zsy.blog.response.Result;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.dto.UserDto;
import com.zsy.blog.dto.UserRequest;
import com.zsy.blog.manager.CaptChaManager;
import com.zsy.blog.manager.SendEmailManager;
import com.zsy.blog.service.UserService;
import com.zsy.blog.utils.FileUtils;
import com.zsy.blog.utils.JwtUtils;
import com.zsy.blog.utils.StringUtils;
import com.zsy.blog.utils.UserUtils;
import com.zsy.blog.vos.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author 郑书宇
 * @create 2023/1/16 17:03
 * @desc
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Api(value = "UserApi",tags = {"用户Api"})
public class UserController {

    private final UserService userService;

    private final SendEmailManager emailManager;

    private final CaptChaManager captChaManager;

    private final AuthenticationManager authenticationManager;

    @Value("${spring.mail.username}")
    private String myEmail;

    @PostMapping("/registered")
    @ApiOperation(value = "用户注册",tags = "用户API")
    public Result registeredUser(
            @ApiParam(value = "用户信息",required = true)
            @Valid @RequestBody UserDto userDto,HttpServletRequest request)
    {

        String emailCode=userDto.getCode();

        if(StringUtils.isEmpty(emailCode)){
            return Result.customize(ResultCode.STR_EMPTY_ERROR.value(),"邮箱验证码不能为空!");
        }

        String code=emailManager.getEmailCodeFromRedis(request.getRemoteAddr());

        if(!emailCode.equals(code)){
            return Result.customize(ResultCode.MATCH_ERROR.value(),"验证码错误!");
        }

        boolean flag = userService.registeredUser(userDto);

        return flag?Result.ok():Result.fail();
    }

    @GetMapping("/logout")
    @ApiModelProperty("退出登录")
    public Result logout(){
        String username= UserUtils.getUserDetails().getUsername();
        userService.logout(username);
        return Result.ok();
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录",tags = "用户API")
    public Result login(@Valid @ApiParam(value = "用户登录request") @RequestBody UserRequest userRequest,HttpServletRequest request){

        String code = captChaManager.validCaptChaCode(request.getRemoteAddr());

        if(code==null){
            throw new GlobalException(ResultCode.PARAMS_ERROR.value(),"验证码可能已过期,请刷新图片");
        }

        if(!code.equals(userRequest.getCode())){
            throw new GlobalException(ResultCode.PARAMS_ERROR.value(),"验证码错误,请重新输入");
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(userRequest.getUsername(),userRequest.getPassword());

        try{
            Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            if(authenticate.isAuthenticated()) {
                String accessToken = JwtUtils.generateAccessToken((UserDetails) authenticate.getPrincipal());
                userService.saveTokenToRedis(accessToken,userRequest.getUsername());
                return Result.ok(accessToken);
            }
            return Result.customize(ResultCode.AUTHENTICATE_ERROR.value(),"登录失败");
        }catch (AuthenticationException exception){
            return Result.customize(ResultCode.AUTHENTICATE_ERROR.value(),"账号或密码错误");
        }

    }


    @PostMapping("/avatar")
    @ApiOperation(value = "上传用户头像",tags = "用户API")
    public Result uploadAvatar(@RequestParam("file") MultipartFile multipartFile){

        if(multipartFile.isEmpty()){
            return Result.fail();
        }

        String originalFilename = multipartFile.getOriginalFilename();

        String fileType=FileUtils.getFileNameSuffix(originalFilename);

        boolean isImage= FileUtils.isImageFile(fileType);

        if(!isImage){
            return Result.customize(ResultCode.FILE_TYPE_ERROR.value(),"这不是个图片文件");
        }

        long size = multipartFile.getSize();
        if(size > 5 * Constants.MB){
            return Result.customize(ResultCode.FILE_SIZE_ERROR.value(),"用户头像不能超出5MB");
        }


        try {
           String path=userService.uploadAvatar(multipartFile.getInputStream(),fileType);

           return Result.ok(path);

        } catch (IOException e) {
            throw new GlobalException(ResultCode.FILE_UPLOAD_ERROR.value(),"文件上传失败!");
        }

    }

    @GetMapping("/validator/token")
    @ApiOperation(value = "验证token并且要是管理员",tags = "用户API")
    public Result validatorToken(String token){

        String username = JwtUtils.verifyTokenAndGetUsername(token);

        String redisUserToken = userService.getRedisUserToken(username);

        if(StringUtils.isEmpty(redisUserToken)) return Result.customize(ResultCode.AUTHORIZE_ERROR.value(),"认证失败,请重新登录获取TOKEN");

        UserVo user = userService.findByUsername(username)
                .orElseThrow(()->new GlobalException(ResultCode.AUTHORIZE_ERROR.value(), "找不到该账号,请检查是否输入有误")).toUserVo();

        if(user.isAdmin() || user.isSuperAdmin()){
            return Result.ok(user);
        }

        return Result.customize(ResultCode.AUTHENTICATE_ERROR.value(),"你的权限不够,请联系管理员获取更高权限!");
    }

    @GetMapping("/captcha")
    @ApiOperation(value = "获取图片验证码",tags = "用户API")
    public Result createCaptChaImage(HttpServletRequest request){
        return Result.ok(captChaManager.createCaptChaImage(request.getRemoteAddr()));
    }

    @GetMapping("/sendEmail")
    @ApiOperation(value = "发送邮件",tags = "用户API")
    public Result sendEmail(HttpServletRequest request, String email){
        if(StringUtils.isEmpty(email)){
            return Result.customize(ResultCode.STR_EMPTY_ERROR.value(),"邮箱不能为空!");
        }
        String EMAIL_MATCH="^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$";

        boolean isEmail = Pattern.matches(EMAIL_MATCH, email);

        if(!isEmail) return Result.customize(ResultCode.MATCH_ERROR.value(),"这不是一个正确的邮箱格式!");

        String code = StringUtils.randomNumber(6);

        boolean result=emailManager.sendMessage(email, myEmail, "ZSY-BLOG注册验证码", "你的验证码为: "+code);

        if(!result) return Result.customize(ResultCode.FAIL.value(),"发送邮箱失败!");

        String ip=request.getRemoteAddr();

        emailManager.saveEmailCodeToRedis(ip,code);

        return Result.ok();
    }

}
