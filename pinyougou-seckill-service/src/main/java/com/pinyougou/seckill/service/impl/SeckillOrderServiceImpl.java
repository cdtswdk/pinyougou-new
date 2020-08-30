package com.pinyougou.seckill.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.model.PayLog;
import com.pinyougou.model.SeckillGoods;
import com.pinyougou.model.SeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

@Service

public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public void add(String userId, Long id) {

        //判断是否存在多个订单
        /*SeckillOrder order = (SeckillOrder) this.redisTemplate.boundHashOps("SeckillOrder").get(userId);
        if(order!=null){
            throw new RuntimeException("已存在未支付订单！");
        }*/

        Object queueId = this.redisTemplate.boundListOps("SeckillGoodsQueue_" + id).rightPop();
        if (queueId == null) {
            throw new RuntimeException("已售罄");
        }

        //根据id获取商品信息
        SeckillGoods goods = (SeckillGoods) this.redisTemplate.boundHashOps("SeckillGoods").get(id);

        if (goods == null || goods.getStockCount() <= 0) {
            throw new RuntimeException("已售罄");
        }
        //订单信息
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setUserId(userId);
        //未支付
        seckillOrder.setStatus("0");
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(goods.getCostPrice());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setSellerId(goods.getSellerId());

        //将订单信息存入到Redis缓存
        this.redisTemplate.boundHashOps("SeckillOrder").put(userId, seckillOrder);
        //商品库存-1
        goods.setStockCount(goods.getStockCount() - 1);
        //如果商品库存为0，则将数据同步到数据库中，并清除Redis中的数据
        if (goods.getStockCount() <= 0) {
            //更新数据库数据
            this.seckillGoodsMapper.updateByPrimaryKeySelective(goods);
            //移除redis中的数据
            this.redisTemplate.boundHashOps("SeckillGoods").delete(id);
        } else {
            //修改redis中数据
            this.redisTemplate.boundHashOps("SeckillGoods").put(id, goods);
        }
    }

    @Override
    public SeckillOrder getOrderByUsername(String username) {
        return (SeckillOrder) this.redisTemplate.boundHashOps("SeckillOrder").get(username);
    }

    @Override
    public void updateOrderStatus(String username, String transaction_id) {
        SeckillOrder order = (SeckillOrder) this.redisTemplate.boundHashOps("SeckillOrder").get(username);
        if (order != null) {
            //更新订单数据，并持久化到数据库中
            order.setPayTime(new Date());
            order.setStatus("1");
            order.setTransactionId(transaction_id);

            //更新数据库
            this.seckillOrderMapper.insertSelective(order);

            //移除Redis缓存中的订单数据
            this.redisTemplate.boundHashOps("SeckillOrder").delete(username);
        }
    }

    @Override
    public void removeRedisOrder(String username) {
        SeckillOrder order = (SeckillOrder) this.redisTemplate.boundHashOps("SeckillOrder").get(username);

        if (order != null) {
            SeckillGoods seckillGoods = (SeckillGoods) this.redisTemplate.boundHashOps("SeckillGoods").get(order.getSeckillId());

            //如果Redis中没有，则从数据库中查询
            if (seckillGoods == null) {
                seckillGoods = this.seckillGoodsMapper.selectByPrimaryKey(order.getSeckillId());
            }

            //商品数量+1
            seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);

            //存入redis
            this.redisTemplate.boundHashOps("SeckillGoods").put(seckillGoods.getId(), seckillGoods);

            //移除订单
            this.redisTemplate.boundHashOps("SeckillOrder").delete(username);
        }
    }
}
