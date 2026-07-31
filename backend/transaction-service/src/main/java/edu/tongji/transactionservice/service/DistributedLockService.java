package edu.tongji.transactionservice.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁与幂等工具
 *
 * 设计要点：
 * 1. 分布式锁基于 Redisson 的 RLock（可重入锁，底层是 Redis + Lua 脚本保证原子性，
 *    watch dog 自动续期，避免业务执行时间超过锁租约导致锁提前释放）。用于串行化
 *    跨 JVM 的并发写操作（如并发充值/打赏），保证临界区互斥。
 * 2. 幂等键基于 Redis SETNX（setIfAbsent）：同一业务单号（如支付宝 outTradeNo）只会被
 *    处理一次；重复请求（支付回调重试、前端重复点击）直接返回"已处理"，避免重复加币/打赏。
 * 3. 两者配合：锁解决"并发同时到达"的互斥，幂等键解决"重试/重复推送"的去重。
 */
@Component
public class DistributedLockService {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLockService.class);

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;

    public DistributedLockService(RedissonClient redissonClient, RedisTemplate<String, Object> redisTemplate) {
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取分布式锁
     * @param key        锁键
     * @param waitSeconds   获取锁的最大等待时间
     * @param leaseSeconds  锁的租约时间（业务未执行完时由 watch dog 自动续期）
     * @return 是否成功获取
     */
    public boolean tryLock(String key, long waitSeconds, long leaseSeconds) {
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(waitSeconds, leaseSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("获取分布式锁被中断, key={}", key);
            return false;
        } catch (Exception e) {
            logger.error("获取分布式锁异常, key={}: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 释放分布式锁（仅释放当前线程持有的锁，避免误删他人锁）
     */
    public void unlock(String key) {
        try {
            RLock lock = redissonClient.getLock(key);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (Exception e) {
            logger.warn("释放分布式锁异常, key={}: {}", key, e.getMessage());
        }
    }

    /**
     * 幂等键：基于 Redis SETNX 实现。
     * @param key         幂等键（如 recharge:done:{outTradeNo}）
     * @param ttlSeconds  键存活时间（通常设为足够覆盖重试窗口，如 24h）
     * @return true=首次写入（放行），false=已存在（重复请求，拦截）
     */
    public boolean acquireIdempotent(String key, long ttlSeconds) {
        try {
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(key, System.currentTimeMillis(), ttlSeconds, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(success);
        } catch (Exception e) {
            logger.error("幂等键写入异常, key={}: {}", key, e.getMessage());
            // 异常时保守放行，依靠分布式锁兜底
            return true;
        }
    }

    /**
     * 查询幂等键是否已存在（用于"处理前先判断是否已处理过"，不直接写入）
     */
    public boolean idempotentExists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logger.warn("查询幂等键异常, key={}: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 标记幂等键（在业务成功处理后再写入，避免业务失败时误阻断后续重试）
     * @param key         幂等键
     * @param ttlSeconds  键存活时间
     */
    public void markIdempotent(String key, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().setIfAbsent(key, System.currentTimeMillis(), ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("标记幂等键异常, key={}: {}", key, e.getMessage());
        }
    }
}
