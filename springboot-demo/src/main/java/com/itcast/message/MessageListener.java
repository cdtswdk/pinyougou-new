package com.itcast.message;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageListener {

    @JmsListener(destination = "queue-text")
    public void textMessageListener(String text) {
        System.out.println("收到的text消息为：" + text);
    }

    @JmsListener(destination = "queue-map")
    public void mapMessageListener(Map<String, String> dataMap) {
        System.out.println("收到的map消息为：" + dataMap);
    }
}
