package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Cart;
import com.pinyougou.model.Item;
import com.pinyougou.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> add(List<Cart> carts, Long itemId, Integer num) {
        //防止空指针
        carts = carts == null ? new ArrayList<>() : carts;
        //1.根据商品SKU ID查询SKU商品信息
        Item item = this.itemMapper.selectByPrimaryKey(itemId);
        //2.获取商家ID
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart = searchSellerCart(carts, sellerId);
        if (cart != null) {
            //5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            OrderItem orderItem = getOrderItem(cart, itemId);
            if (orderItem == null) {
                //5.1. 如果没有，新增购物车明细
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            } else {
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                //数量叠加
                orderItem.setNum(orderItem.getNum().intValue() + num.intValue());
                //价格叠加
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));

                //如果商品的数量小于等于0，则从商品列表中移除该商品
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果该商家的购物车列表小于等于0，则移除该商家
                if (cart.getOrderItemList().size() <= 0) {
                    carts.remove(cart);
                }
            }
        } else {
            //4.如果购物车列表中不存在该商家的购物车
            //4.1 新建购物车对象
            cart = createCart(item, num);
            OrderItem orderItem = createOrderItem(item, num);
            cart.getOrderItemList().add(orderItem);
            //4.2 将新建的购物车对象添加到购物车列表
            carts.add(cart);
        }
        return carts;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> carts) {
        this.redisTemplate.boundHashOps("cartList").put(username, carts);
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> carts = (List<Cart>) this.redisTemplate.boundHashOps("cartList").get(username);
        return carts == null ? new ArrayList<>() : carts;
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> redisCart, List<Cart> cookieCart) {
        for (Cart cart : cookieCart) {
            for (OrderItem orderItem : cart.getOrderItemList()) {
                //将Cookie中的数据逐个添加到Reidis中
                redisCart = add(redisCart, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCart;
    }

    private Cart createCart(Item item, Integer num) {
        Cart cart = new Cart();
        cart.setSellerId(item.getSellerId());
        cart.setSellerName(item.getSeller());
        return cart;
    }

    private OrderItem createOrderItem(Item item, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setNum(num);
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }

    private OrderItem getOrderItem(Cart cart, Long itemId) {
        for (OrderItem orderItem : cart.getOrderItemList()) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 根据sellerId查询当前购物车是否以存在该商家的商品
     *
     * @param carts
     * @param sellerId
     * @return
     */
    private Cart searchSellerCart(List<Cart> carts, String sellerId) {
        for (Cart cart : carts) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }
}
