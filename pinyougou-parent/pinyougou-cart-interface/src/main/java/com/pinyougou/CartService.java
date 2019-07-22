package com.pinyougou;

import entity.Cart;

import java.util.List;

public interface CartService {

    /**
     *
     * @param cart  已有的购物车
     * @param itemId  //商品的ID
     * @param num  //购买数量
     * @return
     */

    public List<Cart> addGoodsToCartList(List<Cart> cart,Long itemId,Integer num);

    List<Cart> findCartListFromRedis(String name);

    void saveCartListFormRedis(String name, List<Cart> newCarts);

    List<Cart> mergeCartList(List<Cart> redisCartList, List<Cart> cookieCartList);
}
