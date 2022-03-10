package com.haoxiang.multilevelcache.cache.controller;

import com.haoxiang.multilevelcache.cache.service.TestCacheService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author haoxiang
 * @version 1.0.0
 * @ClassName TestCacheController.java
 * @Description TODO
 * @createTime 2022年03月10日 17:41:00
 */
@RestController
@RequestMapping("/test")
public class TestCacheController {

    @Resource
    private TestCacheService testCacheService;

    @GetMapping(value = "/getCache")
    public String getCache(String key) {
        return testCacheService.getCache(key);
    }

    @PostMapping(value = "/putCache")
    public String putCache(String key) {
        return testCacheService.putCache(key);
    }

    @DeleteMapping(value = "/evictCache")
    public void evictCache(String key) {
        testCacheService.evictCache(key);
    }
}
