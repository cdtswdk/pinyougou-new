package com.pinyougou.manager.security;

import com.alibaba.fastjson.JSON;
import com.pinyougou.http.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Auther: chendongtao
 * @Date: 2021/9/25 11:24
 * @Description:
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        //失败响应消息
        Result result = new Result(false, "账号或密码不正确！");
        responseLogin(httpServletResponse, result);
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
