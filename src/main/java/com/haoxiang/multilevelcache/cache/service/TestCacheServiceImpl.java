package com.haoxiang.multilevelcache.cache.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author haoxiang
 * @version 1.0.0
 * @ClassName TestCacheServiceImpl.java
 * @Description TODO
 * @createTime 2022年03月10日 13:32:00
 */
@Slf4j
@Service
@CacheConfig(cacheManager = "multilevelCacheManager")
public class TestCacheServiceImpl implements TestCacheService {

    @Cacheable(value = "multi-cache", keyGenerator = "controlGenerator")
    @Override
    public String getCache(String key) {
        log.info("读DB");
        return "test";
    }

    @CachePut(value = "multi-cache", keyGenerator = "controlGenerator")
    @Override
    public String putCache(String key) {
        log.info("写DB");
        return "test";
    }

    @CacheEvict(value = "multi-cache", keyGenerator = "controlGenerator")
    @Override
    public void evictCache(String key) {
        log.info("删除DB");
    }
}
