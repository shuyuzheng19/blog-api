package com.zsy.blog.utils;

import com.zsy.blog.manager.MyUserDetails;
import com.zsy.blog.vos.UserVo;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author 郑书宇
 * @create 2023/1/19 2:08
 * @desc
 */
public class UserUtils {

    public static MyUserDetails getUserDetails(){
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails;
    }

    public static Integer getUserId(){
        return getUserDetails().getUser().getId();
    }

    public static boolean isSuperAdmin(){
        return getUserDetails().getUserVo().isSuperAdmin();
    }

}
