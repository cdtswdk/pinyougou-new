package com.itcast.mq.spring;

import com.itcast.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.Map;

@Component
public class DemoSpringProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;

    /**
     * 发送文本消息
     * @param text
     */
    public void sendTextMessage(String text){
        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(text);
                return textMessage;
            }
        });
    }

    /**
     * 发送map类型消息
     * @param dataMap
     */
    public void sendMapMessage(Map<String,String> dataMap){
        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setObject("userInfo",dataMap);
                return mapMessage;
            }
        });
    }

    /**
     * 发送object类型消息
     * @param user
     */
    public void sendObjectMessage(User user){
        this.jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setObject(user);
                return objectMessage;
            }
        });
    }
}
