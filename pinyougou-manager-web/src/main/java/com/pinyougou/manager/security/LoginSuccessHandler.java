package com.pinyougou.manager.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.http.Result;
import com.pinyougou.manager.service.ManagerService;
import com.pinyougou.model.TbManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @Auther: chendongtao
 * @Date: 2021/9/25 11:00
 * @Description:
 */
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Reference
    private ManagerService managerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        // 更新时间
        String managerId = authentication.getName();
        TbManager manager = this.managerService.findByManagerId(managerId);
        manager.setLastLoginTime(new Date());
        this.managerService.updateManagerById(manager);

        //成功响应消息
        Result result = new Result(true, "/admin/index.html");
        responseLogin(response, result);
    }

    /**
     * 响应用户登录
     *
     * @param response
     * @param result
     * @throws IOException
     */
    public void responseLogin(HttpServletResponse response, Result result) throws IOException {
        //设置编码格式
        response.setContentType("application/json;charset=utf-8");

        //将Result转成JSON字符
        String jsonString = JSON.toJSONString(result);

        //输出数据
        PrintWriter writer = response.getWriter();
        writer.write(jsonString);

        writer.flush();
        writer.close();
    }

}
