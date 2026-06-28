package com.jesper.seckill.controller;

import com.jesper.seckill.bean.SeckillOrder;
import com.jesper.seckill.limit.SlideWindowRateLimiter;
import com.jesper.seckill.bean.User;
import com.jesper.seckill.rabbitmq.MQSender;
import com.jesper.seckill.rabbitmq.SeckillMessage;
import com.jesper.seckill.redis.GoodsKey;
import com.jesper.seckill.redis.RedisService;
import com.jesper.seckill.redis.SeckillKey;
import com.jesper.seckill.result.CodeMsg;
import com.jesper.seckill.result.Result;
import com.jesper.seckill.service.GoodsService;
import com.jesper.seckill.service.OrderService;
import com.jesper.seckill.service.SeckillService;
import com.jesper.seckill.util.MD5Util;
import com.jesper.seckill.util.UUIDUtil;
import com.jesper.seckill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

/**
 * Created by jiangyunxiong on 2018/5/22.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @Autowired
    SlideWindowRateLimiter rateLimiter;

    //做标记，判断该商品是否被处理过了
    private ConcurrentHashMap<Long, Boolean> localOverMap = new ConcurrentHashMap<>();

    /**
     * 获取秒杀地址：秒杀开始后服务端动态生成MD5 hash路径，防止提前脚本刷接口
     */
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(Model model, User user,
                                         @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long now = System.currentTimeMillis();
        if (now < goods.getStartDate().getTime()) {
            return Result.error(CodeMsg.SECKILL_NOT_START);
        }
        if (now > goods.getEndDate().getTime()) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        String path = MD5Util.md5(UUIDUtil.uuid() + goodsId);
        redisService.set(SeckillKey.seckillPath, "" + user.getId() + "_" + goodsId, path);
        return Result.success(path);
    }

    /**
     * GET POST
     * 1、GET幂等,服务端获取数据，无论调用多少次结果都一样
     * 2、POST，向服务端提交数据，不是幂等
     * <p>
     * 将同步下单改为异步下单
     *
     * @param model
     * @param user
     * @param goodsId
     * @param path 秒杀地址hash，从 /seckill/path 接口获取
     * @return
     */
    @RequestMapping(value = "/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, User user,
                                @RequestParam("goodsId") long goodsId,
                                @RequestParam("path") String path) {

        if (!rateLimiter.tryAcquire("seckill", 50, 1)) {
            return  Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }

        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //校验秒杀地址
        String realPath = redisService.get(SeckillKey.seckillPath, "" + user.getId() + "_" + goodsId, String.class);
        if (realPath == null || !realPath.equals(path)) {
            return Result.error(CodeMsg.SECKILL_PATH_ERROR);
        }
        model.addAttribute("user", user);
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);
        if (stock < 0) {
            redisService.incr(GoodsKey.getGoodsStock, "" + goodsId);
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断重复秒杀
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        //入队
        SeckillMessage message = new SeckillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        sender.sendSeckillMessage(message);
        return Result.success(0);//排队中
    }

    /**
     * 系统初始化,将商品信息加载到redis和本地内存
     */
    @Override
    public void afterPropertiesSet() {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        for (GoodsVo goods : goodsVoList) {
            redisService.set(GoodsKey.getGoodsStock, "" + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }

    /**
     * 定时刷新Redis库存（每5分钟），使用SETNX确保仅恢复丢失的key（如Redis重启），不会覆盖运行中的库存值
     */
    @Scheduled(fixedRate = 300000)
    public void refreshStock() {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        for (GoodsVo goods : goodsVoList) {
            redisService.setnx(GoodsKey.getGoodsStock, "" + goods.getId(),
                               String.valueOf(goods.getStockCount()), 0);
        }
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, User user,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long orderId = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(orderId);
    }
}
