package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.manager.service.ManagerService;
import com.pinyougou.model.Seller;
import com.pinyougou.model.TbManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


/**
 * @Auther: chendongtao
 * @Date: 2021/10/27 20:38
 * @Description:
 */
@RestController
@RequestMapping(value = "/management")
public class ManagementController {
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

            TbManager ifExistMan = this.managerService.findByManagerId(manager.getManagerId());
            if (ifExistMan != null) {
                return new Result(false, "登录名已注册，请换一个");
            }
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

    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    public Result updatePassword(@RequestParam(value = "prePwd") String prePwd,
                                 @RequestParam(value = "newPwd") String newPwd,
                                 @RequestParam(value = "cfmPwd") String cfmPwd) {
        try {
            String manageId = SecurityContextHolder.getContext().getAuthentication().getName();
            TbManager manager = this.managerService.findByManagerId(manageId);
            if (!encoder.matches(prePwd, manager.getManagerPwd())) {
                return new Result(false, "原密码输入错误。");
            }
            if (!newPwd.equals(cfmPwd)) {
                return new Result(false, "两次密码输入不一致。");
            }
            //密码应该加密
            manager.setManagerPwd(encoder.encode(newPwd));
            int count = this.managerService.updateManagerById(manager);
            if (count > 0) {
                return new Result(true, "修改密码成功。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "修改密码失败。");
    }
}
