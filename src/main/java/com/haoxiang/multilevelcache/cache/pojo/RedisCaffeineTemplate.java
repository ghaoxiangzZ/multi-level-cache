package com.haoxiang.multilevelcache.cache.pojo;

import cn.hutool.core.bean.BeanUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.haoxiang.multilevelcache.cache.conf.RedisConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author haoxiang
 * @version 1.0.0
 * @ClassName RedisCaffeineCache.java
 * @Description TODO
 * @createTime 2022年03月09日 23:05:00
 */
@Slf4j
@SuppressWarnings("unchecked")
public class RedisCaffeineTemplate extends AbstractValueAdaptingCache {

    private final String name;

    private final RedisTemplate redisTemplate;

    private final Cache<Object, Object> caffeineCache;

    private final RedisConfigProperties properties;


    public RedisCaffeineTemplate(String name, RedisTemplate redisTemplate, Cache caffeineCache, RedisConfigProperties properties) {
        super(properties.getAllowNull());
        this.name = name;
        this.redisTemplate = redisTemplate;
        this.caffeineCache = caffeineCache;
        this.properties = BeanUtil.copyProperties(properties, RedisConfigProperties.class);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = lookup(key);
        if (value != null) {
            return (T) value;
        }
        try {
            value = valueLoader.call();
            Object storeValue = toStoreValue(value);
            put(key, storeValue);
            return (T) value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e.getCause());
        }
    }

    @Override
    public void put(Object key, Object value) {
        log.info("设置缓存key={},value={}", key, value);
        if (Objects.nonNull(properties.getExpireTime())) {
            redisTemplate.opsForValue().set(key, toStoreValue(value), properties.getExpireTime());
        } else {
            redisTemplate.opsForValue().set(key, toStoreValue(value));
        }
        caffeineCache.put(key, value);
    }

    /**
     * 剔除某个缓存
     */
    @Override
    public void evict(Object key) {
        log.info("删除缓存key={}", key);
        redisTemplate.delete(key);
        publish(CacheEvictMessage.builder()
                .cacheName(name)
                .cacheKey(key)
                .build());
        caffeineCache.invalidate(key);
    }

    @Override
    public void clear() {
        // Do Nothing
    }

    /**
     * 查询缓存
     */
    @Override
    protected Object lookup(Object key) {
        Object value = caffeineCache.getIfPresent(key);
        if (value != null) {
            log.info("从 Caffeine 内获取缓存，key: {},value: {}", key, value);
            return value;
        }
        value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            log.info("从 Redis 获取缓存并放入 Caffeine ，key: {} ,value: {}", key, value);
            caffeineCache.put(key, value);
        }
        return value;
    }

    /**
     * @param message 要删除的缓存
     * @description 缓存变更时通知其他节点清理本地缓存
     */
    private void publish(CacheEvictMessage message) {
        redisTemplate.convertAndSend(properties.getChannelTopic(), message);
    }
}
