package com.qburst.microservice.post.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Set value in redis
     *
     * @param key
     * @param value
     * @param ttl
     * @param unit
     */
    public void set(String key, Object value, long ttl, TimeUnit unit) {
        log.debug("Setting Redis key={} ttl={} {}", key, ttl, unit);
        redisTemplate.opsForValue().set(key, value, ttl, unit);
    }

    /**
     * Get value from redis
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            log.debug("Redis MISS for key={}", key);
            return null;
        }
        log.debug("Redis HIT for key={}", key);
        return clazz.cast(value);
    }

    /**
     * Delete value from redis
     *
     * @param key
     */
    public void delete(String key) {
        log.debug("Deleting Redis key={}", key);
        redisTemplate.delete(key);
    }

    /**
     * Check if value exist in redis
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Put hash value
     *
     * @param key
     * @param field
     * @param value
     */
    public void putHash(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * Get HAsh value
     *
     * @param key
     * @param field
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getHash(String key, String field, Class<T> clazz) {
        Object value = redisTemplate.opsForHash().get(key, field);
        return value == null ? null : clazz.cast(value);
    }

}
