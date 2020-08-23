package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.model.Cart;
import com.pinyougou.model.Order;
import com.pinyougou.model.OrderItem;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public int add(Order order) {

        //从缓存中取购物车数据
        List<Cart> carts = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        int acount=0;
        for (Cart cart : carts) {
            Order newOrder = new Order();
            IdWorker idWorker = new IdWorker();
            long orderId = idWorker.nextId();
            //更新时间
            newOrder.setUpdateTime(order.getUpdateTime());
            newOrder.setCreateTime(order.getCreateTime());
            //创建时间
            newOrder.setStatus(order.getStatus());
            //用户id
            newOrder.setUserId(order.getUserId());
            //订单id
            newOrder.setOrderId(orderId);
            //商家id
            newOrder.setSellerId(cart.getSellerId());
            //支付类型
            newOrder.setPaymentType(order.getPaymentType());
            //收货人
            newOrder.setReceiver(order.getReceiver());
            //收货人地址
            newOrder.setReceiverAreaName(order.getReceiverAreaName());
            //收货人手机
            newOrder.setReceiverMobile(order.getReceiverMobile());
            //订单来源 2:pc端
            newOrder.setSourceType("2");

            //订单金额
            double totalFee = 0;
            //购物车明细
            for (OrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setOrderId(orderId);
                orderItem.setId(idWorker.nextId());
                orderItem.setSellerId(cart.getSellerId());
                totalFee+=orderItem.getTotalFee().doubleValue();

                //增加订单明细
                this.orderItemMapper.insertSelective(orderItem);
            }
            newOrder.setPayment(new BigDecimal(totalFee));
            //增加订单信息
            acount = this.orderMapper.insertSelective(newOrder);
        }
        //删除购物车信息
        this.redisTemplate.boundHashOps("cartList").delete(order.getUserId());
        return acount;
    }
}
