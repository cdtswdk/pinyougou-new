package com.pinyougou.user.service;

import com.pinyougou.model.User;

public interface UserService {

    /**
     * 增加User信息
     * @param user
     * @return
     */
    int add(User user);

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    void createCode(String phone);

    /**
     * 校验用户名
     * @param username
     * @return
     */
    int checkByUserName(String username);

    /**
     * 校验手机号
     * @param phone
     * @return
     */
    int checkByMobilePhone(String phone);

    /**
     * 校验验证码是否一致
     * @param phone
     * @param code
     * @return
     */
    Boolean checkByCode(String phone, String code);

    /**
     * 根据用户名获取用户信息
     * @param s
     * @return
     */
    User getUserByUsername(String s);
}
