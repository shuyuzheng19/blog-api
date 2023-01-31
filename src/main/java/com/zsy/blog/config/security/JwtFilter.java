package com.zsy.blog.config.security;

import com.zsy.blog.manager.MyUserDetailsService;
import com.zsy.blog.response.Result;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.utils.HttpUtils;
import com.zsy.blog.utils.JwtUtils;
import com.zsy.blog.utils.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final String TOKEN_PREFIX="Bearer ";

    private final MyUserDetailsService userDetailsService;


    public JwtFilter(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        response.setHeader("Access-Control-Allow-Origin", "*");

        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");

        response.setHeader("Access-Control-Allow-Headers", "*");

        if(request.getMethod().equals("OPTIONS")){

            response.setStatus(HttpStatus.NO_CONTENT.value());

            chain.doFilter(request,response);

            return;
        }

        String tokenHeader=request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.isEmpty(tokenHeader) || !tokenHeader.startsWith(TOKEN_PREFIX)){
            chain.doFilter(request,response);
            return;
        }

        String token=tokenHeader.substring(TOKEN_PREFIX.length());

        String username = JwtUtils.verifyTokenAndGetUsername(token);

        if(username==null){
            HttpUtils.writeJsonToResponse(Result.customize(ResultCode.AUTHENTICATE_ERROR.value(),"你还未登录,请先登录!"),response);
            return;
        }

        String redisToken=userDetailsService.getRedisToken(username);

        if(redisToken==null){
            HttpUtils.writeJsonToResponse(Result.customize(ResultCode.AUTHENTICATE_ERROR.value(),"登录已过期或者已在他地登录,请重新登录!"),response);
            return;
        }

        if(!redisToken.equals(token)){
            HttpUtils.writeJsonToResponse(Result.customize(ResultCode.AUTHENTICATE_ERROR.value(),"签名有误"),response);
            return;
        }


        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request,response);


    }
}