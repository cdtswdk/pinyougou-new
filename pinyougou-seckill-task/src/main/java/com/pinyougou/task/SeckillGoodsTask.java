package com.pinyougou.task;

import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.model.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Set;

@Service
public class SeckillGoodsTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "*/5 * * * * ?")
    public void pushGoodsToRedis() {
        Example example = new Example(SeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        //状态
        criteria.andEqualTo("status", "1");
        //活动时间
        criteria.andCondition("now() BETWEEN start_time AND end_time");

        //库存大于0
        criteria.andGreaterThanOrEqualTo("stockCount", 0);

        //获取Redis中所有的key,实现排除当前Redis中已经存在的商品
        Set<Long> ids = this.redisTemplate.boundHashOps("SeckillGoods").keys();
        if (ids != null && ids.size() > 0) {
            criteria.andNotIn("id", ids);
        }

        List<SeckillGoods> seckillGoods = this.seckillGoodsMapper.selectByExample(example);

        //存入redis
        for (SeckillGoods seckillGood : seckillGoods) {
            this.redisTemplate.boundHashOps("SeckillGoods").put(seckillGood.getId(), seckillGood);

            //把商品的个数信息存入到Redis队列中
            pushId2Queue(seckillGood);
        }

    }

    public void pushId2Queue(SeckillGoods goods) {
        for (int i = 0; i < goods.getStockCount(); i++) {
            this.redisTemplate.boundListOps("SeckillGoodsQueue_" + goods.getId()).leftPush(goods.getId());
        }
    }
}
