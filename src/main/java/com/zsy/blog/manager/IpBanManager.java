package com.zsy.blog.manager;

import com.zsy.blog.common.Constants;
import com.zsy.blog.entitys.IPBan;
import com.zsy.blog.enums.Ban;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author 郑书宇
 * @create 2023/1/21 11:24
 * @desc
 */
@Service
public class IpBanManager {

    @Resource
    private RedisTemplate redisTemplate;

    public IPBan bannedIp(String url,String ip, Ban ban, String message,long time) {
        String md5Ip= DigestUtils.md5DigestAsHex(ip.getBytes());
        LocalDateTime banTime=LocalDateTime.now();
        LocalDateTime restoreTime=banTime.plus(time, ChronoUnit.MINUTES);
        IPBan ipBan=new IPBan();
        ipBan.setId(md5Ip);
        ipBan.setDescription(message);
        ipBan.setBan(ban);
        ipBan.setBanTime(banTime);
        ipBan.setRestoreTime(restoreTime);
        redisTemplate.opsForHash().put(Constants.IP_BAN_MAP,md5Ip+"-"+url,ipBan);
        return ipBan;
    }

    public void restoreIp(String ip) {

    }

    public IPBan getIpBan(String ip, String url) {
        String md5Ip= DigestUtils.md5DigestAsHex(ip.getBytes());
        final String KEY=md5Ip + "-" + url;
        Object result = redisTemplate.opsForHash().get(Constants.IP_BAN_MAP,KEY);
        if(result!=null){

            IPBan ipBan= (IPBan) result;

            if(LocalDateTime.now().isAfter(ipBan.getRestoreTime())){
                redisTemplate.opsForHash().delete(Constants.IP_BAN_MAP,KEY);
                return null;
            }

            return ipBan;

        }
        return null;
    }

    public IPBan isBanAndGetIpBan(String ip,String url,  long time, int total) {

        IPBan ipBan = getIpBan(ip, url);

        if(ipBan!=null){
            return ipBan;
        }

        String md5Ip= DigestUtils.md5DigestAsHex(ip.getBytes());

        final String KEY= Constants.IP_BAN + ":" + md5Ip+":"+url;

        Boolean flag = redisTemplate.hasKey(KEY);

        if(flag) {
            Integer count = (Integer) redisTemplate.opsForValue().get(KEY);
            if(count>=total){
                IPBan ban = bannedIp(url, ip, Ban.FREQUENT_VISITS, "访问频率过快", Constants.IP_BAN_TIME);
                if(ban==null){
                    return null;
                }
                redisTemplate.delete(KEY);
                return ban;
            }
            redisTemplate.opsForValue().set(KEY,count+1,0);
        }else{
            redisTemplate.opsForValue().set(KEY,1,Constants.TIME, TimeUnit.SECONDS);
        }
        return null;
    }
}
