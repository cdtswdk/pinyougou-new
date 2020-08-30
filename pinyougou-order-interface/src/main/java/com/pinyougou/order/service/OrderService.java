package com.pinyougou.order.service;

import com.pinyougou.model.Order;
import com.pinyougou.model.PayLog;

public interface OrderService {

    /**
     * 根据用户名查找订单日志
     *
     * @param username
     * @return
     */
    PayLog searchPayLogFromRedis(String username);

    /**
     * 实现订单增加操作
     *
     * @param order
     * @return
     */
    int add(Order order);

    /**
     * 修改订单状态和日志状态
     *
     * @param out_trade_no
     * @param transaction_id
     */
    void updateOrderStatus(String out_trade_no, String transaction_id);
}
