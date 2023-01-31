package com.zsy.blog.utils;

import com.zsy.blog.common.Constants;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author 郑书宇
 * @create 2023/1/17 13:33
 * @desc
 */
public class DateUtils {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String dateToStr(Date date){
        return dateToStr(date.getTime()/1000);
    }


    public static String dateToStr(LocalDateTime dateTime){
        return dateToStr(dateTime.toEpochSecond(ZoneOffset.of("+8")));
    }

    //将时间转换成易理解的格式
    public static String dateToStr(long unix){

        long now= new Date().getTime()/1000;

        long second=now-unix;

        String dateStr="";

        if(second<=60) {
            dateStr = "刚刚";
        }else if(second>60&&second<=60*60){
            dateStr = second/60 + "分钟前";
        }else if(second>60*60&&second<=60*60*24){
            dateStr = second / 60 / 60  + "小时前";
        }else if(second>60*60*24&&second<=60*60*24*30){
            dateStr = second / 60 / 60 / 24 + "天前";
        }else if(second>60*60*24*30&&second<=60*60*24*30*12){
            dateStr = second / 60 / 60 / 24 / 30 + "月前";
        }else{
            dateStr = second / 60 / 60 / 24 / (30*12) + "年前";
        }

        return dateStr;
    }


    //将时间戳转换为日期格式并格式化 毫秒
    public static String formatMill(long timestamp){
        Date date = new Date(timestamp);
        return SIMPLE_DATE_FORMAT.format(date);
    }

    //将时间戳转换为日期格式并格式化 毫秒
    public static String formatDate(Date date){
        return SIMPLE_DATE_FORMAT.format(date);
    }

    //将时间戳转换为日期格式并格式化 秒
    public static String formatSecond(long timestamp){
        return formatMill(timestamp*1000);
    }

    //将时间戳转换为LocalDateTime
    public static LocalDateTime timeStampToLocalDateTime(long stamp){

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(stamp),Constants.DEFAULT_ZONE);

        return localDateTime;
    }
}
