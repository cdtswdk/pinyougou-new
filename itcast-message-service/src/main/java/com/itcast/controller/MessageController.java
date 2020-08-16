package com.itcast.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/message")
public class MessageController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 消息发送模拟
     * @param code
     * @return
     */
    @RequestMapping(value = "/send")
    public String sendMessage(String code){
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("mobile","13829024910");
        dataMap.put("templateCode","SMS_189216040");
        dataMap.put("signName","乐优商城");
        dataMap.put("param","{\"code\":\""+(int)(Math.random()*10000)+"\"}");

        this.jmsMessagingTemplate.convertAndSend("message-itcast",dataMap);

        return "OK";
    }
}
