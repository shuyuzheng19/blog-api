package com.zsy.blog.manager;

import com.zsy.blog.entitys.User;
import com.zsy.blog.enums.UserStatusEnum;
import com.zsy.blog.vos.UserVo;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author 郑书宇
 * @create 2023/1/16 16:53
 * @desc
 */
@Data
public class MyUserDetails implements UserDetails {

    private User user;

    public MyUserDetails(User user){
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String name = user.getRole().getName();

        if(name.equals("SUPER_ADMIN")) {
            return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()), new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus()== UserStatusEnum.NORMA?true:false;
    }

    public UserVo getUserVo() {
        return user.toUserVo();
    }
}
