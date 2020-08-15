package com.itcast.test;

import com.itcast.domain.User;
import com.itcast.mq.spring.DemoSpringProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-mq.xml")
public class ProducerTest {

    @Autowired
    private DemoSpringProducer demoSpringProducer;

    @Test
    public void testTextMessage(){
        this.demoSpringProducer.sendTextMessage("我抱牛逼啊"+Math.random());
    }

    @Test
    public void testMapMessage(){
        Map<String,String> dataMap = new HashMap<String, String>();
        dataMap.put("username","张三");
        dataMap.put("password","123");
        this.demoSpringProducer.sendMapMessage(dataMap);
    }

    @Test
    public void testObjectMessage(){
        User user = new User("fafhuh和覅哦啊的活佛i啊","123",15);
        this.demoSpringProducer.sendObjectMessage(user);
    }
}
