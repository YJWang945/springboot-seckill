package com.jesper.seckill.redis;

/**
 * Created by jiangyunxiong on 2018/5/29.
 */
public class OrderKey extends BasePrefix {

    private OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public OrderKey(String prefix) {
        super(prefix);
    }
    public static OrderKey getSeckillOrderByUidGid = new OrderKey("seckill");
    public static OrderKey seckillLock = new OrderKey(30, "sl");
}
