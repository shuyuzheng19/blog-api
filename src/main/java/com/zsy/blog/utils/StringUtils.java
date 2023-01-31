package com.zsy.blog.utils;

/**
 * @author 郑书宇
 * @create 2023/1/16 15:37
 * @desc
 */
public class StringUtils {

    public static boolean isEmpty(String param){
        return param==null||param.trim().equals("");
    }


    public static String randomNumber(int size){

        StringBuilder stringBuilder=new StringBuilder();

        for (int i = 0; i < size; i++) {
            int number=(int)((Math.random())*10);

            stringBuilder.append(number);
        }

        return stringBuilder.toString();
    }

    public static boolean anyEmpty(String... params){
        if(params==null || params.length==0){
            return true;
        }

        for (String param : params) {
            if(param==null||param.trim().equals("")){
                return true;
            }
        }

        return false;
    }

}
