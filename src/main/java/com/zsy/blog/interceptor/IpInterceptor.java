package com.zsy.blog.interceptor;

import com.zsy.blog.annos.Delay;
import com.zsy.blog.common.ResultCode;
import com.zsy.blog.entitys.IPBan;
import com.zsy.blog.manager.IpBanManager;
import com.zsy.blog.response.Result;
import com.zsy.blog.utils.HttpUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 郑书宇
 * @create 2023/1/18 11:16
 * @desc
 */
public class IpInterceptor implements HandlerInterceptor {

    @Resource
    private IpBanManager ipBanManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod hm = null;

        try{
            hm=(HandlerMethod) handler;
        }catch (ClassCastException exception){
            return true;
        }

        Delay delay = hm.getMethodAnnotation(Delay.class);

        if(delay!=null){
            String ip=request.getRemoteAddr();
            String path=request.getRequestURI();
            IPBan ban = ipBanManager.isBanAndGetIpBan(ip, path, delay.time(), delay.count());
            if(ban!=null){
                ban.setUrl(path);
                ban.setType(delay.type());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                HttpUtils.writeJsonToResponse(Result.customize(ResultCode.IP_ERROR.value(),ban.getDescription(),ban),response);
                return false;
            }
        }

        return true;
    }
}
