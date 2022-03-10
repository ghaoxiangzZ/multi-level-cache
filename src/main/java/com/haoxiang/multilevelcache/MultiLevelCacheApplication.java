package com.haoxiang.multilevelcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.haoxiang.*")
@EnableCaching
public class MultiLevelCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiLevelCacheApplication.class, args);
    }

}
