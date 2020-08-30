package com.pinyougou.seckill.service;

import com.pinyougou.model.PayLog;
import com.pinyougou.model.SeckillOrder;

public interface SeckillOrderService {

    /**
     * 用户下单
     *
     * @param userId
     * @param id
     */
    void add(String userId, Long id);

    /**
     * 根据用户名查找订单
     *
     * @param username
     * @return
     */
    SeckillOrder getOrderByUsername(String username);

    /**
     * 更新订单信息
     *
     * @param username
     * @param transaction_id
     */
    void updateOrderStatus(String username, String transaction_id);

    /**
     * 从redis中移除订单数据
     *
     * @param username
     */
    void removeRedisOrder(String username);
}
