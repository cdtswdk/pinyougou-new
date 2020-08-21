package com.itcast.mq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class DemoQueueProducer {
    public static void main(String[] args) throws Exception {
        //创建会话工厂对象ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.254.128:61616");
        //创建连接对象Connection
        Connection connection = connectionFactory.createConnection();
        //开启连接对象
        connection.start();
        //获得会话Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建消息
        TextMessage textMessage = session.createTextMessage();
        textMessage.setText("你好啊，张三！！！" + Math.random() * 1000);
        //指定消息发送地址和类型
        Queue queue = session.createQueue("queue-test");
        //创建消息发送对象
        MessageProducer producer = session.createProducer(queue);
        //实现消息发送
        producer.send(textMessage);
        //资源关闭
        session.close();
        connection.close();
    }
}
