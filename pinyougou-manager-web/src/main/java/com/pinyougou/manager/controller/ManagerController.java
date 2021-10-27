package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.manager.service.ManagerService;
import com.pinyougou.model.TbManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Auther: chendongtao
 * @Date: 2021/10/27 20:38
 * @Description:
 */
@RestController
@RequestMapping(value = "/manager")
public class ManagerController {
    @Reference
    private ManagerService managerService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    /***
     * 增加manager数据
     * @param manager
     * 响应数据：success
     *                  true:成功  false：失败
     *           message
     *                  响应的消息
     *
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody TbManager manager) {
        try {

            //密码应该加密
            manager.setManagerPwd(encoder.encode(manager.getManagerPwd()));

            //执行增加
            int acount = this.managerService.add(manager);

            if (acount > 0) {
                //增加成功
                return new Result(true, "增加成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "增加失败");
    }
}
