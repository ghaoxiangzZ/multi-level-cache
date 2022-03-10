package com.haoxiang.multilevelcache.cache.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

/**
 * @author haoxiang
 * @version 1.0.0
 * @ClassName TestCacheService.java
 * @Description TODO
 * @createTime 2022年03月10日 14:13:00
 */
public interface TestCacheService {
    String getCache(String key);

    String putCache(String key);

    void evictCache(String key);
}
