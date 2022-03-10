package com.haoxiang.multilevelcache.cache.pojo;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

/**
 * @author haoxiang
 * @version 1.0.0
 * @ClassName invalidCacheListener.java
 * @Description TODO
 * @createTime 2022年03月09日 23:08:00
 */
@Slf4j
public class CacheEvictListener implements MessageListener {

    private RedisTemplate redisTemplate;

    private Cache caffeineCache;

    public CacheEvictListener(RedisTemplate redisTemplate, Cache caffeineCache) {
        this.redisTemplate = redisTemplate;
        this.caffeineCache = caffeineCache;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        CacheEvictMessage invalidCache = (CacheEvictMessage) redisTemplate.getValueSerializer()
                .deserialize(message.getBody());
        Object channel = redisTemplate.getStringSerializer().deserialize(message.getChannel());
        Optional.ofNullable(invalidCache)
                .map(CacheEvictMessage::getCacheKey)
                .ifPresent(key -> {
                    caffeineCache.invalidate(key);
                    log.info("删除本地缓存，channel: {{}}, cacheKey:{{}}", channel, invalidCache.getCacheKey());
                });
    }
}
