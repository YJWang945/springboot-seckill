package com.jesper.seckill.limit;

import com.jesper.seckill.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 基于Redis ZSET + Lua实现的分布式滑动窗口限流器。
 * 替代单机Guava RateLimiter，多实例部署时共享限额。
 */
@Component
public class SlideWindowRateLimiter {

    @Autowired
    private RedisService redisService;

    /**
     * 每个key独立计数的限流key前缀
     */
    private static final String PREFIX = "rate_limit:";

    /**
     * Lua脚本：滑动窗口计数
     * KEYS[1] - 限流key
     * ARGV[1] - 窗口上限
     * ARGV[2] - 窗口大小(秒)
     * ARGV[3] - 当前毫秒时间戳
     * 返回 1=放行, 0=拒绝
     */
    private static final String SLIDE_WINDOW_SCRIPT =
            "local key = KEYS[1] " +
            "local limit = tonumber(ARGV[1]) " +
            "local window = tonumber(ARGV[2]) " +
            "local now = tonumber(ARGV[3]) " +
            "redis.call('ZREMRANGEBYSCORE', key, 0, now - window * 1000) " +
            "local count = redis.call('ZCARD', key) " +
            "if count >= limit then " +
            "    return 0 " +
            "end " +
            "redis.call('ZADD', key, now, now .. ':' .. redis.call('INCR', key .. ':seq')) " +
            "redis.call('EXPIRE', key, window + 1) " +
            "return 1";

    /**
     * @param key 限流标识（如接口路径）
     * @param limit 窗口内最大请求数
     * @param windowSeconds 窗口大小（秒）
     * @return true=放行, false=限流拒绝
     */
    public boolean tryAcquire(String key, int limit, int windowSeconds) {
        List<String> keys = Collections.singletonList(PREFIX + key);
        List<String> args = List.of(
                String.valueOf(limit),
                String.valueOf(windowSeconds),
                String.valueOf(System.currentTimeMillis())
        );
        Object result = redisService.execLua(SLIDE_WINDOW_SCRIPT, keys, args);
        return "1".equals(String.valueOf(result));
    }
}
