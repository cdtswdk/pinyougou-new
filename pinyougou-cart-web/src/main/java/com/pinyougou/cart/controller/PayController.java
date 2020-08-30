package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.PayLog;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IdWorker idWorker;

    @Reference
    private OrderService orderService;

    @RequestMapping(value = "/createNative")
    public Map createNative() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        PayLog payLog = this.orderService.searchPayLogFromRedis(username);
        if (payLog != null) {
            //return this.weixinPayService.createNative(payLog.getOutTradeNo(), (payLog.getTotalFee()*100)+"");
            return this.weixinPayService.createNative(payLog.getOutTradeNo(), "1");
        }

        //String out_trade_no = this.idWorker.nextId()+ "";
        //String total_fee="1";

        return new HashMap();
    }

    @RequestMapping(value = "/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) throws InterruptedException {
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
                this.orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
                return new Result(true, "支付成功！");
            }
            //每3秒查询一次
            Thread.sleep(3000);
            count++;
            if (count > 10) {
                return new Result(false, "time-out");
            }
        }
    }
}
