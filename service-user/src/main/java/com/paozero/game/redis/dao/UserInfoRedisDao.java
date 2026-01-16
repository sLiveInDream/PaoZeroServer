package com.paozero.game.redis.dao;

import com.paozero.game.constant.RedisUserKeyDefine;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

@Service
public class UserInfoRedisDao {
    public static UserInfoRedisDao Instance;
    @Resource(name = "redis-user")
    private RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init(){
        Instance = this;
    }

    public Map<String, String> getUserInfo(String openId) {
        String key = String.format(RedisUserKeyDefine.USER_INFO_MAP_KEY, openId);
        return redisTemplate.<String, String>opsForHash().entries(key);
    }

    public void setUserInfo(String openId, Map<String, String> userInfo) {
        String key = String.format(RedisUserKeyDefine.USER_INFO_MAP_KEY, openId);
        redisTemplate.<String, String>opsForHash().putAll(key, userInfo);
    }
}
