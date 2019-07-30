package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.CartService;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.service.AddressService;
import entity.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.paddings.TBCPadding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @RequestMapping("/findCartList")
    public List<Cart> findCartList( HttpServletRequest request,HttpServletResponse response) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(name)) {
            String cartListString = CookieUtil.getCookieValue(request, "cartList", true);
            if (StringUtils.isEmpty(cartListString)) {
                //赋以空值,防止CartserviceImpl空指针
                cartListString = "[]";
            }
            List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);
            return cookieCartList;
        } else {
            List<Cart> redisCartList = cartService.findCartListFromRedis(name);
            if (redisCartList==null){
                redisCartList=new ArrayList<>();
            }

            //合并cookieCartList与RedisCartList

            String cartListString = CookieUtil.getCookieValue(request, "cartList", true);
            if (StringUtils.isEmpty(cartListString)) {
                //赋以空值,防止CartserviceImpl空指针
                cartListString = "[]";
            }
            List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);

            if(cookieCartList.size()>0){
               List<Cart> newestCart = cartService.mergeCartList(redisCartList,cookieCartList);
               //最新的购物车保存至redis中
                cartService.saveCartListFormRedis(name,newestCart);
               //清除cookie中的购物车
                CookieUtil.deleteCookie(request,response,"cartList");
                return newestCart;
            }
            return redisCartList;
        }
    }

    @CrossOrigin(origins = {"http://localhost:9105","http://localhost:9106"},allowCredentials = "true")
    @RequestMapping("/add")
    public Result addCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {

        //判断用户是否登入
        try {
            response.setHeader("Access-Control-Allow-Origin","http://localhost:9105"); //统一指定的域访问我的服务器资源
            response.setHeader("Access-Control-Allow-Credentials","true");  //统一客户端携带cookie
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(name)) {
                //匿名登入
                //先从cookie中的购物车
                List<Cart> cartList = findCartList(request,response);
                //加入购物车
                List<Cart> cookieCartList = cartService.addGoodsToCartList(cartList, itemId, num);
                //添加到cookie里面
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cookieCartList), 1 * 24 * 3600,true);
                return new Result(true, "添加购物车成功");

            } else {
                //redis内的数据
                List<Cart> redisCartList = cartService.findCartListFromRedis(name);


                List<Cart> newCarts = cartService.addGoodsToCartList(redisCartList, itemId, num);
                //重新储存值redis
                cartService.saveCartListFormRedis(name,newCarts);
                return new Result(true, "添加购物车成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加购物车失败");
        }


    }



}
