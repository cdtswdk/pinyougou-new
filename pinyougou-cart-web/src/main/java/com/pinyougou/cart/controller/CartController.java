package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.http.Result;
import com.pinyougou.model.Cart;
import com.pinyougou.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping(value = "/add")
    @CrossOrigin(origins = "http://localhost:18088", allowCredentials = "true")
    public Result add(Long itemId, Integer num) {

        //解决跨域问题
        //response.setHeader("Access-Control-Allow-Origin", "http://localhost:18088");
        //response.setHeader("Access-Control-Allow-Credentials", "true");

        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);

        //（1）从cookie中取出购物车
        List<Cart> carts = getList();
        //（2）向购物车添加商品
        carts = this.cartService.add(carts, itemId, num);

        if (username.equals("anonymousUser")) {
            //（3）将购物车存入cookie
            String cartList = JSON.toJSONString(carts);
            CookieUtil.setCookie(request, response, "cartList", cartList, 3600 * 24, "UTF-8");
        } else {
            //存入Redis
            //如果是已登录，保存到redis
            this.cartService.saveCartListToRedis(username, carts);
        }
        return new Result(true, "加入购物车成功！");
    }

    /**
     * 获取购物车数据
     *
     * @return
     */
    @RequestMapping(value = "/list")
    private List<Cart> getList() {
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //从cookie中获取数据
        String cartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        //防止空指针
        if (cartList == null || cartList.equals("")) {
            cartList = "[]";
        }
        List<Cart> cookieCarts = JSON.parseArray(cartList, Cart.class);
        if (username.equals("anonymousUser")) {
            return cookieCarts == null ? new ArrayList<>() : cookieCarts;
        } else {
            List<Cart> redisCarts = this.cartService.findCartListFromRedis(username);
            if (redisCarts != null && redisCarts.size() > 0) {
                //合并操作
                redisCarts = this.cartService.mergeCartList(redisCarts, cookieCarts);
                //清除cookie数据
                CookieUtil.deleteCookie(request, response, "cartList");
                //更新redis
                this.cartService.saveCartListToRedis(username, redisCarts);
            }
            return redisCarts;
        }
    }
}
