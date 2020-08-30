package com.pinyougou.seckill.service;

import com.pinyougou.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    /**
     * 查询秒杀商品列表
     *
     * @return
     */
    List<SeckillGoods> list();

    /**
     * 根据id获取商品
     *
     * @param id
     * @return
     */
    SeckillGoods getOne(Long id);
}
