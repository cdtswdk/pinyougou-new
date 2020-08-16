package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.User;
import com.pinyougou.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @RequestMapping(value = "sendCode")
    public Result createCode(String phone){
        try {
            this.userService.createCode(phone);
            return new Result(true,"发送验证码成功！");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Result(false,"发送验证码失败！");
    }


    /**
     * 增加User数据
     * @param user
     * 响应数据：success
     *                  true:成功  false：失败
     *           message
     *                  响应的消息
     *
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody User user,String code) {
        try {
            //校验用户名是否重复
            int ucount = this.userService.checkByUserName(user.getUsername());
            if(ucount>0){
                return new Result(false,"用户名已经存在！");
            }
            //判断手机号是否存在
            int mcount = this.userService.checkByMobilePhone(user.getPhone());
            if(mcount>0){
                return new Result(false,"手机号已经存在！");
            }
            //判断验证码是否一致
            Boolean flag = this.userService.checkByCode(user.getPhone(),code);
            if(!flag){
                return new Result(false,"验证码输入错误！");
            }

            //执行增加
            int acount = userService.add(user);

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
