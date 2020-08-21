package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.model.User;
import com.pinyougou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.DigestUtils;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${template_code}")
    private String template_code;

    @Value("${sign_name}")
    private String sign_name;

    /**
     * 增加User信息
     *
     * @param user
     * @return
     */
    @Override
    public int add(User user) {

        //当前时间
        Date date = new Date();
        user.setCreated(date);
        user.setUpdated(date);

        //密码加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        int icount = userMapper.insertSelective(user);
        //增加成功，则让验证码失效
        if (icount > 0) {
            this.redisTemplate.boundHashOps("MobileInfo").delete(user.getPhone());
        }
        return icount;
    }

    @Override
    public void createCode(String phone) {
        //生成4位随机数
        String code = (int) (Math.random() * 10000) + "";

        //存入缓存
        this.redisTemplate.boundHashOps("MobileInfo").put(phone, code);
        //发送验证码到activeMQ
        sendMsg(phone, code);

    }

    @Override
    public int checkByUserName(String username) {
        User user = new User();
        user.setUsername(username);
        return this.userMapper.selectCount(user);
    }

    @Override
    public int checkByMobilePhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        return this.userMapper.selectCount(user);
    }

    @Override
    public Boolean checkByCode(String phone, String code) {
        String redisCode = (String) this.redisTemplate.boundHashOps("MobileInfo").get(phone);
        if (redisCode == null || !redisCode.equals(code)) {
            return false;
        }
        return true;
    }

    @Override
    public User getUserByUsername(String s) {
        User user = new User();
        user.setUsername(s);
        return this.userMapper.selectOne(user);
    }

    private void sendMsg(String phone, String code) {
        this.jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();

                mapMessage.setString("templateCode", template_code);
                mapMessage.setString("signName", sign_name);
                mapMessage.setString("mobile", phone);
                //存储验证码
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("code", code);
                mapMessage.setString("param", JSON.toJSONString(dataMap));
                return mapMessage;
            }
        });
    }

}
