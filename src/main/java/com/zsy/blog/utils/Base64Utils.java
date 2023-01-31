package com.zsy.blog.utils;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * @author 郑书宇
 * @create 2023/1/21 11:08
 * @desc
 */
public class Base64Utils {

    public static String encoding(byte[] bytes){
        byte[] encode = Base64.getEncoder().encode(bytes);

        return new String(encode);
    }

    public static String encodingToString(String str){
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String encodingToString(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String encoding(String str, Charset charset){

        return Base64.getEncoder().encodeToString(str.getBytes(charset));
    }

    public static byte[] decoding(String str){
        return Base64.getDecoder().decode(str.getBytes());
    }

    public static String decodingToString(String str){
        byte[] decode = Base64.getDecoder().decode(str.getBytes());
        return new String(decode);
    }

    public static String decodingToString(String str,Charset charset){
        byte[] decode = Base64.getDecoder().decode(str.getBytes());
        return new String(decode,charset);
    }

}
