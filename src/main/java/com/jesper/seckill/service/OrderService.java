package com.jesper.seckill.service;

import com.jesper.seckill.bean.OrderInfo;
import com.jesper.seckill.bean.SeckillOrder;
import com.jesper.seckill.bean.User;
import com.jesper.seckill.mapper.GoodsMapper;
import com.jesper.seckill.mapper.OrderMapper;
import com.jesper.seckill.rabbitmq.MQSender;
import com.jesper.seckill.redis.GoodsKey;
import com.jesper.seckill.redis.OrderKey;
import com.jesper.seckill.redis.RedisService;
import com.jesper.seckill.redis.SeckillKey;
import com.jesper.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;

/**
 * Created by jiangyunxiong on 2018/5/23.
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    // 订单状态：已取消
    private static final int STATUS_CANCELLED = 6;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    GoodsMapper goodsMapper;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    public SeckillOrder getOrderByUserIdGoodsId(long userId, long goodsId) {
        return redisService.get(OrderKey.getSeckillOrderByUidGid, "" + userId + "_" + goodsId, SeckillOrder.class);
    }

    public OrderInfo getOrderById(long orderId) {
        return orderMapper.getOrderById(orderId);
    }

    /**
     * 因为要同时分别在订单详情表和秒杀订单表都新增一条数据，所以要保证两个操作是一个事物
     */
    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderMapper.insert(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        orderMapper.insertSeckillOrder(seckillOrder);

        redisService.set(OrderKey.getSeckillOrderByUidGid, "" + user.getId() + "_" + goods.getId(), seckillOrder);

        // 订单创建成功后发送延迟取消消息（15分钟未支付自动取消），事务提交后才发送
        long orderId = orderInfo.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sender.sendOrderCancelMessage(orderId);
            }
        });

        return orderInfo;
    }

    /**
     * 取消订单并恢复库存：仅取消新建未支付的订单
     */
    @Transactional
    public void cancelOrder(long orderId) {
        OrderInfo order = orderMapper.getOrderById(orderId);
        if (order == null) {
            log.warn("cancelOrder failed, order not found: {}", orderId);
            return;
        }
        if (order.getStatus() != 0) {
            return;
        }
        orderMapper.updateStatus(orderId, STATUS_CANCELLED);
        goodsMapper.recoverStock(order.getGoodsId());
        redisService.incr(GoodsKey.getGoodsStock, "" + order.getGoodsId());
        redisService.delete(SeckillKey.isGoodsOver, "" + order.getGoodsId());
        log.info("Order cancelled, orderId={}, goodsId={}", orderId, order.getGoodsId());
    }

}
