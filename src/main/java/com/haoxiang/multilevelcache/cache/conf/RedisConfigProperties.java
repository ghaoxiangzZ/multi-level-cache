package com.haoxiang.multilevelcache.cache.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author haoxiang
 * @version 1.0.0
 * @ClassName CacheConfigProperties.java
 * @Description TODO
 * @createTime 2022年03月09日 23:09:00
 */
@Data
@ConfigurationProperties(prefix = "cache.redis")
public class RedisConfigProperties {

    /**
     * 是否允许控制，主要用于缓存穿透
     */
    private Boolean allowNull = true;

    /**
     * 缓存过期时间
     */
    private Duration expireTime = Duration.ofMinutes(10);

    /**
     * redis发布订阅的主题，用于删除缓存
     */
    private String channelTopic = "cache:redis:caffeine:topic";
}
