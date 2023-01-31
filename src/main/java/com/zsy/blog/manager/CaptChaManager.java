package com.zsy.blog.manager;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.zsy.blog.common.GlobalException;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.utils.Base64Utils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author 郑书宇
 * @create 2023/1/16 23:30
 * @desc
 */
@Service
public class CaptChaManager {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DefaultKaptcha defaultKaptcha;

    public String createCaptChaImage(String ip){

        String code = defaultKaptcha.createText();

        BufferedImage image = defaultKaptcha.createImage(code);

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();

        try {
            ImageIO.write(image,"png",byteArrayOutputStream);
        } catch (IOException e) {
            throw new GlobalException(ResultCode.ERROR.value(),"验证码生成出错");
        }

        String md5Ip= DigestUtils.md5DigestAsHex(ip.getBytes());

        stringRedisTemplate.opsForValue().set("captcha-code->"+md5Ip,code,60,TimeUnit.SECONDS);

        String imageBase64 = Base64Utils.encodingToString(byteArrayOutputStream.toByteArray());

        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageBase64;
    }

    public String validCaptChaCode(String ip){

        String md5Ip= DigestUtils.md5DigestAsHex(ip.getBytes());

        return stringRedisTemplate.opsForValue().get("captcha-code->"+md5Ip);
    }
}
