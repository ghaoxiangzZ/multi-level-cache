package com.haoxiang.multilevelcache.cache.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author haoxiang
 * @version 1.0.0
 * @ClassName CacheConfigProperties.java
 * @Description TODO
 * @createTime 2022年03月09日 23:09:00
 */
@Data
@ConfigurationProperties(prefix = "cache.caffeine")
public class CaffeineConfigProperties {

    private int initSize = 10;
    private int maxSize = 1000;
    private Duration expireTime = Duration.ofMinutes(10);
}
