package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.manager.service.ManagerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/***
 *
 * @Author:shenkunlin
 * @Description:itheima
 * @date: 2018/9/5 16:24
 *
 ****/
@RestController
@RequestMapping(value = "/login")
public class LoginController {

    @Reference
    private ManagerService managerService;

    /**
     * 获取登录用户名
     *
     * @return
     */
    @RequestMapping(value = "/name")
    public String getLoginName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * 获取登录时间
     */
    @RequestMapping(value = "/lastLoginTime")
    public String getLastLoginTime() {
        String managerId = SecurityContextHolder.getContext().getAuthentication().getName();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date lastLoginTime = this.managerService.getLastLoginTime(managerId);
        return sdf.format(lastLoginTime);
    }
}
