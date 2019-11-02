package com.freeman.common.auth.shiro.cache;

import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.utils.serializer.ObjectSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** 重写Shiro缓存管理器 */
@Slf4j
@Deprecated
public class FmRedisCacheManager implements CacheManager {
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap();
    private JedisDao redis; /*@Autowired private RedisDao redis;*/
    private RedisSerializer valueSerializer = new ObjectSerializer();
    private String keyPrefix = "shiro:cache:";
    private int expire = 1800; //秒

    public FmRedisCacheManager(JedisDao redis) { this.redis = redis; }
    public FmRedisCacheManager(JedisDao redis, RedisSerializer valueSerializer) {
        this.redis = redis;
        this.valueSerializer = valueSerializer;
    }
    public FmRedisCacheManager(JedisDao redis, RedisSerializer valueSerializer, String keyPrefix, int expire) {
        this.redis = redis;
        this.valueSerializer = valueSerializer;
        this.keyPrefix = keyPrefix;
        this.expire = expire;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        log.debug("get cache, name=" + name);
        Cache cache = (Cache)this.caches.get(name);
        if (cache == null) {
            cache = new FmRedisCache<K, V>(this.redis, valueSerializer,this.keyPrefix + name + ":", expire);
            this.caches.put(name, cache);
        }

        return (Cache)cache;
    }


    public void setRedis(JedisDao redis) { this.redis = redis; }

    public void setValueSerializer(RedisSerializer valueSerializer) { this.valueSerializer = valueSerializer; }

    public void setKeyPrefix(String keyPrefix) { this.keyPrefix = keyPrefix; }

    public void setExpire(int expire) { this.expire = expire; }
}






