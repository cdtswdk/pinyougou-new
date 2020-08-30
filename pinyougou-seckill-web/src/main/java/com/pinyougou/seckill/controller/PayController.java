package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.PayLog;
import com.pinyougou.model.SeckillOrder;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService orderService;

    @RequestMapping(value = "/createNative")
    public Map createNative() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SeckillOrder seckillOrder = this.orderService.getOrderByUsername(username);
        if (seckillOrder != null) {
            //return this.weixinPayService.createNative(payLog.getOutTradeNo(), (payLog.getTotalFee()*100)+"");
            return this.weixinPayService.createNative(seckillOrder.getId().toString(), "1");
        }

        //String out_trade_no = this.idWorker.nextId()+ "";
        //String total_fee="1";

        return new HashMap();
    }

    @RequestMapping(value = "/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) throws InterruptedException {
        //获取用户登录名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        int count = 0;
        while (true) {
            Map<String, String> map = this.weixinPayService.queryPayStatus(out_trade_no);

            //支付异常
            if (map == null) {
                return new Result(false, "支付发生错误！");
            }

            //支付成功
            if (map.get("trade_state").equals("SUCCESS")) {
                //修改支付状态和订单状态
                this.orderService.updateOrderStatus(username, map.get("transaction_id"));
                return new Result(true, "支付成功！");
            }

            //每3秒查询一次
            Thread.sleep(3000);
            count++;
            if (count > 10) {
                //关闭微信订单
                Map<String, String> closeResult = this.weixinPayService.closePay(out_trade_no);
                //如果关闭成功，redis数据库回滚
                if (closeResult.get("result_code").equals("SUCCESS")) {
                    //查询redis
                    this.orderService.removeRedisOrder(username);
                } else {
                    //订单已支付，修改订单状态
                    if (closeResult.get("err_code").equals("ORDERPAID")) {
                        //查询订单状态
                        map = weixinPayService.queryPayStatus(out_trade_no);
                        //更新订单状态
                        this.orderService.updateOrderStatus(username, map.get("transaction_id"));
                        return new Result(true, "支付成功！");
                    }
                }
                return new Result(false, "time-out");
            }
        }
    }
}
