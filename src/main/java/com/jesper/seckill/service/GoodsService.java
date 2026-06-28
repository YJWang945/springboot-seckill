package com.jesper.seckill.service;

import com.jesper.seckill.bean.SeckillGoods;
import com.jesper.seckill.mapper.GoodsMapper;
import com.jesper.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jiangyunxiong on 2018/5/22.
 */
@Service
public class GoodsService {

    private static final Logger log = LoggerFactory.getLogger(GoodsService.class);

    //乐观锁冲突最大重试次数
    private static final int DEFAULT_MAX_RETRIES = 5;

    @Autowired
    GoodsMapper goodsMapper;

    /**
     * 查询商品列表
     */
    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoodsVo();
    }

    /**
     * 根据id查询指定商品
     */
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 减少库存，每次减一。乐观锁冲突时重试，DB异常重试后仍然失败则向上抛出。
     */
    public boolean reduceStock(GoodsVo goods) {
        int numAttempts = 0;
        int ret = 0;
        SeckillGoods sg = new SeckillGoods();
        sg.setGoodsId(goods.getId());
        do {
            numAttempts++;
            try {
                sg.setVersion(goodsMapper.getVersionByGoodsId(goods.getId()));
                ret = goodsMapper.reduceStockByVersion(sg);
            } catch (DataAccessException e) {
                log.warn("Reduce stock error on attempt {}/{}, goodsId={}",
                         numAttempts, DEFAULT_MAX_RETRIES, goods.getId(), e);
                if (numAttempts >= DEFAULT_MAX_RETRIES) {
                    throw e;
                }
                continue;
            }
            if (ret != 0) {
                break;
            }
        } while (numAttempts < DEFAULT_MAX_RETRIES);

        return ret > 0;
    }
}
