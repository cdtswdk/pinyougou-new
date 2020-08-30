package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.Order;
import com.pinyougou.order.service.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(value = "/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping(value = "/add")
    public Result add(@RequestBody Order order) {

        //用户名称
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUserId(username);

        //订单状态
        order.setStatus("1");

        //创建时间和更新时间
        order.setCreateTime(new Date());
        order.setUpdateTime(order.getCreateTime());

        try {
            //增加
            int count = this.orderService.add(order);
            if (count > 0) {
                return new Result(true, "订单增加成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "订单增加失败！");
    }
}
