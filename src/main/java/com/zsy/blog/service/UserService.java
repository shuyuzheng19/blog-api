package com.zsy.blog.service;

import com.zsy.blog.dto.UserDto;
import com.zsy.blog.entitys.User;

import java.io.InputStream;
import java.util.Optional;

/**
 * @author 郑书宇
 * @create 2023/1/16 16:50
 * @desc
 */
public interface UserService {

   Optional<User> findByUsername(String username);

   boolean registeredUser(UserDto userDto);

   String uploadAvatar(InputStream inputStream,String type);

   void saveTokenToRedis(String token,String username);

   String getRedisUserToken(String username);

   void logout(String username);



}
