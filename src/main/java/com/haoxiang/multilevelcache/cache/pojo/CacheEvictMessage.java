package com.haoxiang.multilevelcache.cache.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author haoxiang
 * @version 1.0.0
 * @ClassName DelCacheMessage.java
 * @Description TODO
 * @createTime 2022年03月09日 23:07:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheEvictMessage {

    private String cacheName;

    private Object cacheKey;
}
