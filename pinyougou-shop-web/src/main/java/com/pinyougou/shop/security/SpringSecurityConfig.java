package com.pinyougou.shop.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.http.Result;
import com.pinyougou.sellergoods.service.SellerService;
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
 * @date: 2018/9/5 18:12
 *
 ****/
@Component
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Reference
    private SellerService sellerService;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    /****
     * 1、放行配置
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //放行地址
        web.ignoring().antMatchers("/css/**");
        web.ignoring().antMatchers("/img/**");
        web.ignoring().antMatchers("/js/**");
        web.ignoring().antMatchers("/plugins/**");
        web.ignoring().antMatchers("/*.html");
        web.ignoring().antMatchers("/seller/add.shtml");
    }

    /***
     * 2、权限拦截配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //限制访问
        http.authorizeRequests()
                .antMatchers("/**").access("hasAnyRole('ADMIN')");

        //禁用CSRF
        http.csrf().disable();

        //发生异常
        http.exceptionHandling().accessDeniedPage("/error.html");

        //启用iframe
        http.headers().frameOptions().disable();

        //一个用户只允许在一个地方登录，其他用户登录就会把已登录用户挤掉
        http.sessionManagement().maximumSessions(1).expiredUrl("/shoplogin.html");

        //配置登录
        /*http.formLogin().loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        // 更新时间
                        String sellerId = authentication.getName();
                        Seller seller = sellerService.getOneById(sellerId);
                        seller.setLastLoginTime(new Date());
                        sellerService.updateSellerById(seller);

                        //成功响应消息
                        Result result = new Result(true, "/admin/index.html");

                        responseLogin(response, result);
                    }
                }).failureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                //失败响应消息
                Result result = new Result(false, "账号或密码不正确！");
                responseLogin(response, result);
            }
        });*/
        http.formLogin().loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .successHandler(this.loginSuccessHandler)
                .failureHandler(this.loginFailureHandler);

        //配置登出
        http.logout().logoutUrl("/logout")
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/shoplogin.html");
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    /***
     * 3、授权认证
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //写死
        //auth.inMemoryAuthentication().withUser("admin").password("123456").roles("ADMIN");
        //自定义授权认证类
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(encoder);  //指定加密对象
    }
}
