package com.zsy.blog.manager;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 郑书宇
 * @create 2023/1/16 21:23
 * @desc
 */
@Service
public class SendEmailManager {

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public boolean sendMessage(String toEmail, String sendEmail, String subject, String content){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        simpleMailMessage.setFrom(sendEmail);
        try{
            javaMailSender.send(simpleMailMessage);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public void saveEmailCodeToRedis(String ip,String code){
        stringRedisTemplate.opsForValue().set("email-code->"+ip,code,2, TimeUnit.MINUTES);
    }

    public String getEmailCodeFromRedis(String ip){
        return stringRedisTemplate.opsForValue().get("email-code->"+ip);
    }

}
