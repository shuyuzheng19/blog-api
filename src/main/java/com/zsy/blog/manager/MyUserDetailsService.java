package com.zsy.blog.manager;

import com.zsy.blog.entitys.User;
import com.zsy.blog.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 郑书宇
 * @create 2023/1/16 15:43
 * @desc
 */
@Component
public class MyUserDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("找不到该账号"));
        return new MyUserDetails(user);
    }

    public String getRedisToken(String username){
        return userService.getRedisUserToken(username);
    }
}
