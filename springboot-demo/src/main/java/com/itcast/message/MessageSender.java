package com.itcast.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageSender {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;


    public void sendTextMessage(String text) {
        this.jmsMessagingTemplate.convertAndSend("queue-text", text);
    }

    public void sendMapMessage() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("username", "zhangsan");
        dataMap.put("password", "123");

        this.jmsMessagingTemplate.convertAndSend("queue-map", dataMap);
    }
}
