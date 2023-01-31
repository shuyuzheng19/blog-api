package com.zsy.blog.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zsy.blog.common.Constants;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public class JwtUtils {

    //加密规则
    private static final Algorithm DEFAULT_ALGORITHM= Algorithm.HMAC256("wafkasjfaosifjasionf");

    //生成token
    public static String generateAccessToken(UserDetails userDetails){
        return JWT.create()
                .withClaim("username",userDetails.getUsername())
                .withClaim("role",userDetails.getAuthorities().stream().map(role->role.getAuthority()).collect(Collectors.toList()))
                .withKeyId(UUID.randomUUID().toString())
                .withExpiresAt(new Date(System.currentTimeMillis()+(1000*60*60)* Constants.TOKEN_EXPIRE_TIME_HOURS))
                .withSubject(userDetails.getUsername())
                .withJWTId(UUID.randomUUID().toString())
                .sign(DEFAULT_ALGORITHM);
    }

    //验证token并返回该用户名 如果验证失败则返回null
    public static String verifyTokenAndGetUsername(String token){
        JWTVerifier verifier = JWT.require(DEFAULT_ALGORITHM).build();
        try{
            DecodedJWT verify = verifier.verify(token);
            String username = verify.getSubject();
            return username;
        }catch (Exception e){
            return null;
        }
    }
}