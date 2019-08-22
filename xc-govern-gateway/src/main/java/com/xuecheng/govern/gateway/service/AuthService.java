package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 在头信息中找到令牌信息
     * @param request
     * @return
     */
    public String findHeader(HttpServletRequest request){
        //取出头信息
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            //如果头信息为空,则返回null
            return null;
        }
        //头信息中令牌模型为:
        //Bearer  eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6IlhjV2ViQXBwIiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOm51bGwsInV0eXBlIjpudWxsLCJpZCI6bnVsbCwiZXhwIjoxNTY2MjE5OTI1LCJqdGkiOiJiNjU5MzlhMC0wN2EwLTQ1NmYtOWFiNS03YzA4N2I3ZjE5YTQiLCJjbGllbnRfaWQiOiJYY1dlYkFwcCJ9.k_Yn_X14LqqjGgUo_Ip6jMoaR3JQ2_mKIo7Axmk1WeXo-z1hNeLST3WaYXeUASS833ONMTw2PONTWGPFjxbS3GE_3xEf6t5BsCLqC90aBESKZGYSf72L-rxhakj93y6siqiaruTeJpoLbxX8ykXAlSDa56kkrGWqXfnaa2sJnHAo0XXxgVFlBbFpJCmSCDrsaPtkihJMlQ-S-wR1g2EXr-qs_a-m5cjsp3EXzXe-pvBLTNY_Wk6t1gzPkiL5Qo6gjrvpRL6IjvVoXGNiJEnfVfaNACSVop0heHz7zjEfaUpdSXj-1p0uG4oynkMvGjwbGf4YD7Pc8Hk8u8JDqcsFgQ
        //值还有个空格,索引再进行判断如果不是以Bearer 开头的就返回null
        if(!authorization.startsWith("Bearer ")){
            return null;
        }
        //截取真正的令牌信息后返回
        String jwt = authorization.substring(7);
        return jwt;
    }

    /**
     * 在cookie中查询令牌信息
     * @param request
     * @return
     */
    public String findCookie(HttpServletRequest request){
        //使用cookie 的工具类来查询到map集合
        Map<String, String> uid = CookieUtil.readCookie(request, "uid");
        //根据键找到值
        String uid1 = uid.get("uid");
        if(StringUtils.isEmpty(uid1)){
            return null;
        }
        return uid1;
    }

    /**
     * 在redis中查询令牌有效时间,返回的是毫秒值
     * @param access_token
     * @return
     */
    public Long findRedis(String access_token){
        //指定key
        String key = "user_token:"+access_token;
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire;
    }
}
