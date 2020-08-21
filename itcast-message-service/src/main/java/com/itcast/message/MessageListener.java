package com.itcast.message;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageListener {

    @Autowired
    private MessageSender messageSender;

    @JmsListener(destination = "message-list")
    public void readMessage(Map<String, String> dataMap) throws ClientException {
        String mobile = dataMap.get("mobile");
        String signName = dataMap.get("signName");
        String templateCode = dataMap.get("templateCode");
        String param = dataMap.get("param");
        //消息发送
        this.messageSender.sendSms(mobile, signName, templateCode, param);
    }
}
