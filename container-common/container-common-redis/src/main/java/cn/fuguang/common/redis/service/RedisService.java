package cn.fuguang.common.redis.service;


import cn.fuguang.exception.ContainerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 **/
@SuppressWarnings(value = {"unchecked","rawtypes"})
@Component
@Slf4j
public class RedisService
{
    @Resource
    public RedisTemplate redisTemplate;


    public String getKey(String key){
        try {
            return (String) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("redis getKey异常 key e:"+ key, e);
            throw ContainerException.REDIS_ERROR.newInstance("getKey异常 key:" + key);
        }
    }

    public void set(String key, String value){
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("redis set异常 key e:"+ key, e);
            throw ContainerException.REDIS_ERROR.newInstance("set异常 key:" + key);
        }
    }


    public void hset(String key, String hashKey,Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("redis hset异常 key e:{} hashKey:{}", key, hashKey, e);
            throw ContainerException.REDIS_ERROR.newInstance("hset异常 key:" + key);
        }
    }

    public Object hget(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("redis hset异常 key e:{} hashKey:{}", key, hashKey, e);
            throw ContainerException.REDIS_ERROR.newInstance("hset异常 key:" + key);
        }
    }

    public void increment(String key, int i) {
        try {
            redisTemplate.opsForValue().increment(key, i);
        } catch (Exception e) {
            log.error("redis increment异常 key e:"+ key, e);
            throw ContainerException.REDIS_ERROR.newInstance("increment异常 key:" + key);
        }
    }

    public void expire(String key, long timeout) {
        try {
            redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis expire异常 key e:"+ key, e);
            throw ContainerException.REDIS_ERROR.newInstance("expire异常 key:" + key);
        }
    }
}
