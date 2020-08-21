package com.pinyougou.cart.service;

import com.pinyougou.model.Cart;

import java.util.List;

public interface CartService {

    /**
     * 添加购物车
     * @param carts
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> add(List<Cart> carts,Long itemId,Integer num);

    /**
     * 添加购物车到redis
     * @param username
     * @param carts
     */
    void saveCartListToRedis(String username,List<Cart> carts);

    /**
     * 从购物车查询数据
     * @return
     */
    List<Cart> findCartListFromRedis(String username);

    /**
     * 合并购物车
     * @param redisCart
     * @param cookieCart
     * @return
     */
    List<Cart> mergeCartList(List<Cart> redisCart,List<Cart> cookieCart);
}
