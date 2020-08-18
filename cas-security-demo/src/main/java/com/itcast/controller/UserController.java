package com.itcast.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @RequestMapping(value = "/findLoginUser")
    public String findUser(Model model){
        //获取用户登录名字
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("name",name);
        System.out.println(name);
        return name;
    }
}
