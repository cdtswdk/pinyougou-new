package com.itcast.controller;

import com.itcast.domain.User;
import com.itcast.mapper.UserMapper;
import com.itcast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;

    @RequestMapping(value = "/hello")
    public String hello(){

        String url = environment.getProperty("url");
        return "hel "+url;
    }

    @RequestMapping(value = "/findAll")
    public List<User> findAll(){
        List<User> users = this.userService.findAll();
        for (User user : users) {
            System.out.println(user);
        }
        return users;
    }
}
