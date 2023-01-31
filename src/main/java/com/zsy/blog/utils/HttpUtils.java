package com.zsy.blog.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpUtils {

    public static void writeJsonToResponse(Object data, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");

        try {
            new ObjectMapper().writeValue(response.getOutputStream(),data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}