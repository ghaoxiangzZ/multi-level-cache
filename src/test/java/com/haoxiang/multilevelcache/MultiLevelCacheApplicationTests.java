package com.haoxiang.multilevelcache;

import com.haoxiang.multilevelcache.cache.service.TestCacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MultiLevelCacheApplicationTests {

    @Autowired
    private TestCacheService testCacheService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testGet() {
        testCacheService.getCache("test-v2");
    }

    @Test
    public void testPut() {
        testCacheService.putCache("test-v2");
    }

    @Test
    public void testEvict() {
        testCacheService.evictCache("test");
    }
}
