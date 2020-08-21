package com.itcast.mq.spring;

import com.itcast.domain.User;

import javax.jms.*;
import java.util.Map;

public class SpringMessageListener implements MessageListener {
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            //将消息转成文本消息
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println("获取到的内容：" + textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;
            try {
                Map<String, String> dataMap = (Map<String, String>) mapMessage.getObject("userInfo");
                System.out.println(dataMap);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                User user = (User) objectMessage.getObject();
                System.out.println(user);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
