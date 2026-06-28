package com.jesper.seckill.rabbitmq;

import com.jesper.seckill.bean.SeckillOrder;
import com.jesper.seckill.bean.User;
import com.jesper.seckill.redis.OrderKey;
import com.jesper.seckill.redis.RedisService;
import com.jesper.seckill.service.GoodsService;
import com.jesper.seckill.service.OrderService;
import com.jesper.seckill.service.SeckillService;
import com.jesper.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jiangyunxiong on 2018/5/29.
 */
@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);


    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @RabbitListener(queues=MQConfig.SECKILL_QUEUE)
    public void receive(String message){
        log.info("receive message:"+message);
        SeckillMessage m = RedisService.stringToBean(message, SeckillMessage.class);
        User user = m.getUser();
        long goodsId = m.getGoodsId();

        // SETNX幂等锁，防止同一用户对同一商品的并发消费
        String lockKey = "" + user.getId() + "_" + goodsId;
        if (!redisService.setnx(OrderKey.seckillLock, lockKey, "1", 30)) {
            return;
        }
        try {
            GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
            int stock = goodsVo.getStockCount();
            if(stock <= 0){
                return;
            }

            //判断重复秒杀
            SeckillOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
            if(order != null) {
                return;
            }

            //减库存 下订单 写入秒杀订单
            seckillService.seckill(user, goodsVo);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            log.warn("Duplicate seckill order, userId={}, goodsId={}", user.getId(), goodsId);
        } finally {
            redisService.delete(OrderKey.seckillLock, lockKey);
        }
    }

    @RabbitListener(queues = MQConfig.ORDER_CANCEL_QUEUE)
    public void receiveOrderCancel(String message) {
        long orderId = Long.parseLong(message);
        log.info("receive order cancel message, orderId:" + orderId);
        orderService.cancelOrder(orderId);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info(" topic  queue1 message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info(" topic  queue2 message:" + message);
    }
}
