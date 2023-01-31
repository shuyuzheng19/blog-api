package com.zsy.blog.service.impl;

import com.zsy.blog.common.Constants;
import com.zsy.blog.common.GlobalException;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.dto.UserDto;
import com.zsy.blog.entitys.User;
import com.zsy.blog.repository.UserRepository;
import com.zsy.blog.service.UserService;
import com.zsy.blog.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 郑书宇
 * @create 2023/1/16 16:52
 * @desc 用户服务
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RedisTemplate redisTemplate;

    @Value("${web.upload-path}")
    private String filePath;

    @Value("${web.hostname}")
    private String hostName;

    @Override
    public Optional<User> findByUsername(String username) {

        final String KEY=Constants.USER + ":" + username;

        Boolean flag = redisTemplate.hasKey(KEY);

        if(flag){

            Object result= redisTemplate.opsForValue().get(KEY);

            return Optional.of((User) result);
        }

        Optional<User> user = userRepository.findByUsername(username);

        if(user.get()!=null){
            redisTemplate.opsForValue().set(KEY,user.get());
        }

        return user;
    }

    @Override
    public boolean registeredUser(UserDto userDto) {

        if(userRepository.existsByUsername(userDto.getUsername())){
            throw new GlobalException(ResultCode.PARAMS_ERROR.value(), "该账号已存在,请重新输入!");
        }

        User user=userDto.toUserDo();

        String encodingPassword=passwordEncoder.encode(user.getPassword());

        user.setPassword(encodingPassword);

        User result = userRepository.save(user);

        return result.getId()==null?false:true;

    }

    @Override
    public String uploadAvatar(InputStream inputStream,String type) {
        String fileName= UUID.randomUUID().toString().replace("-","")+"-"+System.currentTimeMillis()+"."+type;
        try {
            FileUtils.saveFile(inputStream,new File(filePath+"/avatars/"+fileName));
        } catch (IOException e) {
            throw new GlobalException(ResultCode.FILE_UPLOAD_ERROR.value(),"文件上传失败!");
        }

        String path="http://"+hostName+"/static/avatars/"+fileName;

        return path;
    }

    @Override
    public void saveTokenToRedis(String token, String username) {

        String md5Username= DigestUtils.md5DigestAsHex(username.getBytes());

        redisTemplate.opsForValue().set(Constants.LOGIN_USER_TOKEN+":"+md5Username,token,Constants.TOKEN_EXPIRE_TIME_HOURS, TimeUnit.HOURS);

    }

    @Override
    public String getRedisUserToken(String username) {
        String md5Username= DigestUtils.md5DigestAsHex(username.getBytes());
        String token = (String) redisTemplate.opsForValue().get(Constants.LOGIN_USER_TOKEN+":"+md5Username);
        return token;
    }

    @Override
    public void logout(String username) {
        String md5Username= DigestUtils.md5DigestAsHex(username.getBytes());
        redisTemplate.delete(Constants.LOGIN_USER_TOKEN+":"+md5Username);
    }

}
