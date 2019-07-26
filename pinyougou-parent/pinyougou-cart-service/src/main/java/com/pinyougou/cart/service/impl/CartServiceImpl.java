package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //找到添加购物车的商品信息
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        String sellerId = tbItem.getSellerId();
        Cart c = findCartBySellerId(sellerId, cartList);

        //判断之前购物车是否已添加该商品
        if (c == null) {

            c = new Cart();

            //设置Cart信息:sellerID,sellerName,orderItemList
            c.setSellerId(tbItem.getSellerId());
            c.setSellerName(tbItem.getSeller());

            //设置购物车明细
            List<TbOrderItem> list = new ArrayList<>();
            TbOrderItem tbOrderItem = new TbOrderItem();
            //设置id
            tbOrderItem.setItemId(tbItem.getId());
            tbOrderItem.setGoodsId(tbItem.getGoodsId());
            tbOrderItem.setTitle(tbItem.getTitle());
            tbOrderItem.setPrice(tbItem.getPrice());
            tbOrderItem.setSellerId(sellerId);

            tbOrderItem.setNum(num);
            double v = num * tbItem.getPrice().doubleValue();
            tbOrderItem.setTotalFee(new BigDecimal(v));
            tbOrderItem.setPicPath(tbItem.getImage());
            list.add(tbOrderItem);
            c.setOrderItemList(list);
            cartList.add(c);
        } else {
            //cart不为空
            List<TbOrderItem> orderItemList = c.getOrderItemList();
            //根据itemID判断单个商品是否添加到购物车
            TbOrderItem tbOrderItem = findOrderItemByItemId(itemId, orderItemList);
            if (tbOrderItem == null) {
                tbOrderItem = new TbOrderItem();
                //商品列表没有该项
                tbOrderItem.setItemId(tbItem.getId());
                tbOrderItem.setGoodsId(tbItem.getGoodsId());
                tbOrderItem.setTitle(tbItem.getTitle());
                tbOrderItem.setPrice(tbItem.getPrice());
                tbOrderItem.setSellerId(sellerId);
                tbOrderItem.setNum(num);
                double v = num * tbItem.getPrice().doubleValue();
                tbOrderItem.setTotalFee(new BigDecimal(v));
                tbOrderItem.setPicPath(tbItem.getImage());
                orderItemList.add(tbOrderItem);
            } else {
                //商品列表有该项
                Integer num1 = tbOrderItem.getNum() + num;
                tbOrderItem.setNum(num1);
                double v = num1 * tbItem.getPrice().doubleValue();
                tbOrderItem.setTotalFee(new BigDecimal(v));

                if (num1 < 1) {
                    orderItemList.remove(tbOrderItem);
                }
                if (orderItemList.size() == 0) {
                    cartList.remove(c);
                }

            }

        }

        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> findCartListFromRedis(String name) {
        List<Cart> redisCartList = (List<Cart>) redisTemplate.boundHashOps("REDIS_CARTLIST").get(name);
        if(redisCartList==null){
            redisCartList=new ArrayList<>();
        }
        return redisCartList;
    }

    @Override
    public void saveCartListFormRedis(String name, List<Cart> newCarts) {
        redisTemplate.boundHashOps("REDIS_CARTLIST").put(name, newCarts);
    }

    /**
     * 合并cookie与redis购物车
     *
     * @param redisCartList
     * @param cookieCartList
     * @return
     */

    @Override
    public List<Cart> mergeCartList(List<Cart> redisCartList, List<Cart> cookieCartList) {
        for (Cart cart : cookieCartList) {
            for (TbOrderItem tbOrderItem : cart.getOrderItemList()) {
                redisCartList = addGoodsToCartList(redisCartList, tbOrderItem.getItemId(), tbOrderItem.getNum());
            }
        }
        return redisCartList;
    }

    /**
     * 删除
     *
     * @param itemId
     */


    private TbOrderItem findOrderItemByItemId(Long itemId, List<TbOrderItem> orderItemList) {
        for (TbOrderItem tbOrderItem : orderItemList) {
            if (itemId.equals(tbOrderItem.getItemId())) {
                return tbOrderItem;
            }

        }
        return null;
    }


    private Cart findCartBySellerId(String sellerId, List<Cart> cartList) {
        if (cartList != null) {
            for (Cart cart : cartList) {
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }
            }
        }
        return null;
    }
}
