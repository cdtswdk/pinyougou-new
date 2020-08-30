package com.pinyougou.user.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.model.Address;

import java.util.List;

public interface AddressService {

    /**
     * 根据用户名查询地址列表
     *
     * @param userId
     * @return
     */
    List<Address> getAddressListByUserId(String userId);
}
