package com.itcast.mq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class DemoQueueConsumer {
    public static void main(String[] args) throws Exception{
        //创建连接工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.254.128:61616");
        //创建连接对象
        Connection connection = connectionFactory.createConnection();
        //开启链接
        connection.start();
        //创建对话对象session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建需要接受的消息地址
        Queue queue = session.createQueue("queue-test");
        //接收消息
        MessageConsumer consumer = session.createConsumer(queue);

        //监听消息，监听的方式是创建了一个线程
        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                if(message instanceof TextMessage){
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("消息内容："+textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //为了防止主线程结束，这里录入键盘事件后再继续执行
        System.in.read();
        //关闭资源
        session.close();
        connection.close();
    }
    public static void main_1(String[] args) throws Exception{
        //创建连接工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.254.128:61616");
        //创建连接对象
        Connection connection = connectionFactory.createConnection();
        //开启链接
        connection.start();
        //创建对话对象session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建需要接受的消息地址
        Queue queue = session.createQueue("queue-test");
        //接收消息
        MessageConsumer consumer = session.createConsumer(queue);
        while (true){
            Message message = consumer.receive(10000);
            if(message!=null){
                if(message instanceof TextMessage){
                    TextMessage textMessage = (TextMessage) message;
                    System.out.println(textMessage.getText());
                    break;
                }
            }
        }
        //关闭资源
        session.close();
        connection.close();
    }
}
