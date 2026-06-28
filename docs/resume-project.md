# 高并发秒杀系统 — 简历项目描述

## 项目概述

基于 Spring Boot 3 + MyBatis + Redis + RabbitMQ + MySQL 构建的限时抢购平台，核心目标是在瞬时高并发下保证库存不超卖、系统不崩溃。

**技术栈**：Spring Boot 3.4、JDK 17、MyBatis 3、Redis(Jedis + Lua)、RabbitMQ(死信队列)、Druid、Thymeleaf、BCrypt

---

## 核心工作

### 1. 设计五层削峰秒杀链路，解决高并发下库存超卖

```
限流(Redis+Lua滑动窗口) → 内存标记(ConcurrentHashMap)
  → Redis DECR原子预扣 → RabbitMQ异步削峰
    → DB乐观锁(version + stock_count > 0，冲突重试5次)
```

每一层为下一层挡流量。客户端轮询 `/seckill/result` 获取最终结果（orderId / -1 失败 / 0 排队中）。库存扣减用 `version` 字段做 CAS，配合 `stock_count > 0` 双重条件防超卖。

### 2. 基于 Redis + Lua 实现分布式滑动窗口限流

- 替代单机 Guava RateLimiter，多实例共享限额
- Lua 脚本原子执行 `ZREMRANGEBYSCORE → ZCARD → 阈值判断 → ZADD`
- 用 ZSET 记录请求时间戳，滑动窗口统计，窗口外自动清理

### 3. 基于 RabbitMQ 死信队列实现订单超时取消与库存回滚

- 下单成功后投递消息到 `order.delay.queue`（`x-message-ttl=15min` + `x-dead-letter-exchange`）
- 消息过期后自动转入 `order.cancel.queue`，消费者判断订单状态
- `TransactionSynchronizationManager.afterCommit()` 保证事务提交后才投递，避免回滚后误取消
- 取消后恢复：DB `stock_count + 1` + Redis `INCR` + 清除 `isGoodsOver` 标记

### 4. 秒杀地址隐藏 + SETNX 幂等锁 + DB 唯一索引，三层防刷防重

- **地址隐藏**：秒杀前不暴露真实 URL，开始时动态生成 MD5 hash 地址（按用户隔离，60s TTL）
- **SETNX 幂等锁**：MQ 消费端用 `SET NX EX` 串行化同一用户+商品的并发处理
- **DB 唯一索引**：`sk_order(user_id, goods_id)` 作为最后防线，异常捕获后 ack 消息避免无效重试

---

## 其他技术细节

- **密码安全**：BCrypt 替代 MD5，存量密码登录时自动识别并平滑升级
- **参数解析**：自定义 `HandlerMethodArgumentResolver`，从 Cookie + Redis 透明注入登录态
- **库存自愈**：`@Scheduled` 定时任务 + SETNX，Redis 重启后 5 分钟内自动从 DB 恢复库存
- **页面缓存**：Thymeleaf 手动渲染 HTML 存入 Redis，商品列表接口 QPS 提升一个数量级
- **DB 异常重试**：乐观锁冲突时重试，`DataAccessException` 重试耗尽后向上抛出触发 MQ 重试，非 DB 异常直接传播不吞错

---

## 面试要点

| 高频问点 | 准备思路 |
|---------|---------|
| Redis 预扣和 DB 库存不一致？ | 最终一致性：Redis 是预扣（快速失败），DB 是最终裁决，定时任务 + SETNX 做自动恢复 |
| 为什么不用分布式锁而用乐观锁？ | 乐观锁无锁等待、吞吐更高。热点商品加锁反而成为瓶颈 |
| MQ 消息丢失怎么办？ | 持久化队列 + 持久化消息 + 消费重试 + `afterCommit` 保证投递时机 |
| 限流算法选型？ | 滑动窗口比令牌桶更精确，Redis Lua 比单机 Guava 更适合分布式 |
| 怎么压测的？ | 核心指标：库存扣减正确性（总下单数 = 库存数）、P99 延迟、MQ 积压量 |
