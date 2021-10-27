package com.pinyougou.manager.security;

import com.alibaba.fastjson.JSON;
import com.pinyougou.http.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/***
 *
 * @Author:shenkunlin
 * @Description:itheima
 * @date: 2018/9/5 15:59
 *
 ****/
@Component
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    /**
     * 公开链接
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //忽略公开链接的授权管理
        web.ignoring()
                .antMatchers("/css/**")
                .antMatchers("/img/**")
                .antMatchers("/js/**")
                .antMatchers("/plugins/**")
                .antMatchers("/login.html")
                .antMatchers("/register.html")
                .antMatchers("/error.html")
                .antMatchers("/manager/add.shtml");
    }


    /****
     * 登录后能访问的链接配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //用户登录后访问控制
        http.authorizeRequests()
                //其他所有链接需要ADMIN角色权限才能访问
                .antMatchers("/**").access("hasAnyRole('ADMIN')");

        //CSRF禁用
        http.csrf().disable();

        //异常处理
        http.exceptionHandling().accessDeniedPage("/error.html");

        //启用iframe
        //默认情况，SpringSecurity禁止使用iframe，需要将iframe一些禁止选项关闭
        http.headers().frameOptions().disable();

        //一个用户只能在一个地方登录
        http.sessionManagement().maximumSessions(1).expiredUrl("/login.html");

        //登录
        /*http.formLogin()
                .loginPage("/login.html")   //登录页面
                .loginProcessingUrl("/login")   //登录处理地址
                .defaultSuccessUrl("/admin/index.html", true)    //登录成功后总是跳转到这个页面
                .failureUrl("/login.html");     //登录失败跳转地址*/

        //登录
        http.formLogin()
                .loginPage("/login.html")   //登录页面
                .loginProcessingUrl("/login")   //登录处理地址
                .successHandler(this.loginSuccessHandler)
                .failureHandler(this.loginFailureHandler);

        //登出
        http.logout()
                .invalidateHttpSession(true)        //登出后销毁session
                .logoutUrl("/logout")               //登出处理地址
                .logoutSuccessUrl("/login.html");   //登出成功后跳转地址
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


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    /***
     * 授权配置
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /*auth.inMemoryAuthentication()
                .withUser("admin")   //创建用户，用户名为 admin
                .password("123")             //用户名的密码是123456
                .roles("ADMIN");  */             //用户的角色是ADMIN

        //自定义授权认证类
        auth.userDetailsService(userDetailsService);
    }
}
