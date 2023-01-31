package com.zsy.blog.config.security;

import com.zsy.blog.common.Constants;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.interceptor.IpInterceptor;
import com.zsy.blog.manager.MyUserDetailsService;
import com.zsy.blog.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 郑书宇
 * @create 2023/1/16 15:27
 * @desc 安全配置
 */

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig implements WebMvcConfigurer {

    private final AuthenticationConfiguration authenticationConfiguration;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipInterceptor()).addPathPatterns(
                "/api/v1/blogs/topic",
                "/api/v1/blogs/list",
                "/api/v1/blogs/range",
                "/api/v1/blogs/search",
                "/api/v1/auth/**",
                "/api/v1/file/**"
        );
    }

    @Bean
    public IpInterceptor ipInterceptor(){
        return new IpInterceptor();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable()
                .authorizeHttpRequests(http->{
                    try {
                        http.antMatchers(HttpMethod.OPTIONS).permitAll()
                                .antMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
                                .antMatchers("/api/v1/super/**","/api/v1/system/**").hasRole("SUPER_ADMIN")
                                .antMatchers(
                                        "/api/v1/auth/**","/api/v1/upload/**","/api/v1/file/get",
                                        "/api/v1/blogs/user/star","/api/v1/users/logout").authenticated()
                                .anyRequest().permitAll()
                                .and()
                                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                })
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint((request,response,exception)->{
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    Map<String,Object> map=new HashMap<>();
                    map.put("code", ResultCode.AUTHENTICATE_ERROR.value());
                    map.put("error_url",request.getRequestURL());
                    map.put("message","检测到您还未登录,请登陆后重试");
                    HttpUtils.writeJsonToResponse(map,response);
                })
                .and()
                .exceptionHandling().accessDeniedHandler((request,response,exception)->{
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    Map<String,Object> map=new HashMap<>();
                    map.put("code",ResultCode.AUTHORIZE_ERROR);
                    map.put("error_url",request.getRequestURL());
                    map.put("message","访问被服务器拒绝,可能因为的您的权限不足,请联系管理员");
                    HttpUtils.writeJsonToResponse(map,response);
                })
                .and()
                .build();
    }

    @Bean
    public MyUserDetailsService userDetailsService(){
        return new MyUserDetailsService();
    }

    @Bean
    public JwtFilter jwtFilter(){
        return new JwtFilter(userDetailsService());
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService());

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
