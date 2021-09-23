package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther: chendongtao
 * @Date: 2021/9/22 22:32
 * @Description:
 */
@RestController
@RequestMapping(value = "/login")
public class LoginController {

    @Reference
    private SellerService sellerService;

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
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date lastLoginTime = this.sellerService.getLastLoginTime(sellerId);
        return sdf.format(lastLoginTime);
    }
}
