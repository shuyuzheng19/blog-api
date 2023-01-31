package com.zsy.blog.utils;

import com.zsy.blog.common.Constants;

import java.io.*;

/**
 * @author 郑书宇
 * @create 2023/1/16 19:51
 * @desc
 */
public class FileUtils {

    //判断该文件名称是否为图片文件
    public static boolean isImageFile(String type){

        if(StringUtils.isEmpty(type)){
            return false;
        }

        return Constants.IMAGE_TYPES.contains(type);
    }

    //获取文件后缀名
    public static String getFileNameSuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf(".")+1);
    }

    //传输文件
    public static void saveFile(InputStream inputstream, File file) throws IOException {
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        saveFile(inputstream,new FileOutputStream(file));
    }

    //传输文件
    public static void saveFile(InputStream inputstream, OutputStream outputstream) throws IOException {
        BufferedInputStream bufferedInputStream=new BufferedInputStream(inputstream);
        int len=-1;
        byte[] buff=new byte[4096];
        while((len=bufferedInputStream.read(buff))!=-1){
            outputstream.write(buff,0,len);
        }
        outputstream.flush();
        outputstream.close();
        inputstream.close();
    }

    public static String inputStreamToString(InputStream inputstream) throws IOException {
        int len=-1;
        byte[] buff=new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        while((len=inputstream.read(buff))!=-1){
            byteArrayOutputStream.write(buff,0,len);
        }
        String result=byteArrayOutputStream.toString();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        inputstream.close();
        return result;
    }

    public static void main(String[] args) {
        String txt="a.png";
        System.out.println(getFileNameSuffix(txt));
    }

}
