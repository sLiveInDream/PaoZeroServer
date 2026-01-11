package com.paozero.game.redis.dao;

import com.paozero.game.constant.RedisKeyDefine;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Service
public class ChannelRouterRedisDao {
    public static ChannelRouterRedisDao Instance;
    @Resource(name = "redis-system")
    private RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        Instance = this;
    }


    /**
     * 强制设置正确的路由，只能在gateway层，用户连接时设置
     *
     * @param channelKey
     * @param serviceName
     * @param address
     */
    public void putRouterAddress(String channelKey, String serviceName, String address){
        String key = String.format(RedisKeyDefine.CHANNEL_ROUTER_MAP_KEY, channelKey);
        redisTemplate.opsForHash().put(key, serviceName, address);
        redisTemplate.expire(key, RedisKeyDefine.CHANNEL_ROUTER_MAP_EXPIRE_DAYS, TimeUnit.DAYS);
    }

    /**
     * CAS操作设置路由
     *
     * @param channelKey
     * @param serviceName
     * @param address
     * @return
     */
    public String putRouterAddressIfAbsent(String channelKey, String serviceName, String address) {
        String key = String.format(RedisKeyDefine.CHANNEL_ROUTER_MAP_KEY, channelKey);
        boolean success = redisTemplate.opsForHash().putIfAbsent(key, serviceName, address);
        redisTemplate.expire(key, RedisKeyDefine.CHANNEL_ROUTER_MAP_EXPIRE_DAYS, TimeUnit.DAYS);
        if (success) {
            return address;
        }

        return redisTemplate.<String, String>opsForHash().get(key, serviceName);
    }

    public String getRouterAddress(String channelKey, String serviceName) {
        String key = String.format(RedisKeyDefine.CHANNEL_ROUTER_MAP_KEY, channelKey);
        return redisTemplate.<String, String>opsForHash().get(key, serviceName);
    }

    public void removeRouterAddress(String channelKey) {
        String key = String.format(RedisKeyDefine.CHANNEL_ROUTER_MAP_KEY, channelKey);
        redisTemplate.delete(key);
    }

    public void batchRemoveRouterAddress(Collection<String> channelKeys) {
        redisTemplate.delete(channelKeys);
    }
}
