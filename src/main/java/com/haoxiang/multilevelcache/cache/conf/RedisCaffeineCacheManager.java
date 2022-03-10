package com.haoxiang.multilevelcache.cache.conf;

import com.haoxiang.multilevelcache.cache.pojo.RedisCaffeineTemplate;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author haoxiang
 * @version 1.0.0
 * @ClassName RedisCaffeineCacheManager.java
 * @Description TODO
 * @createTime 2022年03月09日 23:09:00
 */
public class RedisCaffeineCacheManager implements CacheManager {

    private ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();

    private RedisConfigProperties properties;

    private RedisTemplate redisTemplate;

    private com.github.benmanes.caffeine.cache.Cache caffeineCache;

    public RedisCaffeineCacheManager(RedisTemplate redisTemplate,
                                     com.github.benmanes.caffeine.cache.Cache caffeineCache,
                                     RedisConfigProperties cacheRedisCaffeineProperties) {
        this.properties = cacheRedisCaffeineProperties;
        this.redisTemplate = redisTemplate;
        this.caffeineCache = caffeineCache;
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = cacheMap.get(name);
        if (Objects.nonNull(cache)) {
            return cache;
        }
        cache = new RedisCaffeineTemplate(name, redisTemplate, this.caffeineCache, this.properties);
        cacheMap.put(name, cache);
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.cacheMap.keySet();
    }

    public com.github.benmanes.caffeine.cache.Cache getCaffeineCache() {
        return caffeineCache;
    }

    public RedisConfigProperties getProperties() {
        return properties;
    }
}
