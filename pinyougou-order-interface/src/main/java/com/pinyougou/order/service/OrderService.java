package com.pinyougou.order.service;

import com.pinyougou.model.Order;

public interface OrderService {

    /**
     * 实现订单增加操作
     * @param order
     * @return
     */
    int add(Order order);
}
