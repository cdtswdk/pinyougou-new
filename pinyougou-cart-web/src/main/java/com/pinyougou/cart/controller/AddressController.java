package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.Address;
import com.pinyougou.user.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    @RequestMapping(value = "/user/list")
    public List<Address> findAddressListByUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.addressService.getAddressListByUserId(username);
    }
}
