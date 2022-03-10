package com.haoxiang.multilevelcache.cache.conf;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.haoxiang.multilevelcache.cache.pojo.CacheEvictListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author haoxiang
 * @version 1.0.0
 * @ClassName RedisConfig.java
 * @Description TODO
 * @createTime 2022年03月09日 23:11:00
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = {
        RedisConfigProperties.class,
        CaffeineConfigProperties.class
})
public class RedisCaffeineAutoConfigure {

    @Primary
    @Bean("redisCacheManager")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                          RedisConfigProperties redisConfigProperties) {
        RedisCacheConfiguration redisCacheCfg = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(redisConfigProperties.getExpireTime())
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheCfg)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = "caffeineCache")
    public Cache<String, Object> caffeineCache(CaffeineConfigProperties caffeineConfigProperties) {
        return Caffeine.newBuilder()
                .expireAfterAccess(caffeineConfigProperties.getExpireTime())
                .initialCapacity(caffeineConfigProperties.getInitSize())
                .maximumSize(caffeineConfigProperties.getMaxSize())
                .softValues()
                .build();
    }

    @Bean("multilevelCacheManager")
    public RedisCaffeineCacheManager redisCaffeineCacheManager(@Autowired RedisTemplate redisTemplate,
                                                               @Autowired Cache<String, Object> caffeineCache,
                                                               RedisConfigProperties properties) {
        return new RedisCaffeineCacheManager(redisTemplate, caffeineCache, properties);
    }

    @Bean
    public RedisMessageListenerContainer cacheDelListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                   MessageListenerAdapter messageListenerAdapter,
                                                                   RedisConfigProperties properties) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, new ChannelTopic(properties.getChannelTopic()));
        return container;
    }

    @Bean
    public MessageListenerAdapter cacheDelListenerAdapter(@Autowired RedisTemplate redisTemplate,
                                                          @Autowired RedisCaffeineCacheManager cacheManager) {
        Cache caffeineCache = cacheManager.getCaffeineCache();
        CacheEvictListener cacheDelListener = new CacheEvictListener(redisTemplate, caffeineCache);
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
        messageListenerAdapter.setDelegate(cacheDelListener);
        return messageListenerAdapter;
    }

    @Bean("controlGenerator")
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder builder = new StringBuilder();
            builder.append("sys:abps:strategy:")
                    .append(params[0]);
            return builder.toString();
        };
    }
}
