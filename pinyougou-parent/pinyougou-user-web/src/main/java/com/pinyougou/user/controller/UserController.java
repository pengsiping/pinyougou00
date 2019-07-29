package com.pinyougou.user.controller;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.service.UserService;

import entity.Cart;
import entity.Error;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;


import com.github.pagehelper.PageInfo;
import entity.Result;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;

	@CrossOrigin(origins = "http://localhost:9107",allowCredentials = "true")
	@RequestMapping("/moveToMyFavorites")
	public Result moveToMyFavorites(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response){
		//判断用户是否登入
		try {
			System.out.println("cart传递数据过来。。。");
			response.setHeader("Access-Control-Allow-Origin","http://localhost:9107"); //统一指定的域访问我的服务器资源
			response.setHeader("Access-Control-Allow-Credentials","true");  //统一客户端携带cookie
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			if ("anonymousUser".equals(name)) {
				//匿名登入
				//先从cookie中的购物车
				List<Cart> cartList = findCartList(request,response);
				//加入购物车
				List<Cart> cookieCartList = userService.addGoodsToCartList(cartList, itemId, num);
				//添加到cookie里面
				CookieUtil.setCookie(request, response, "myFavoritesList", JSON.toJSONString(cookieCartList), 1 * 24 * 3600,true);
				return new Result(true, "添加关注成功");

			} else {
				//redis内的数据
				List<Cart> redisCartList = userService.findCartListFromRedis(name);


				List<Cart> newCarts = userService.addGoodsToCartList(redisCartList, itemId, num);
				//重新储存值redis
				userService.saveCartListFormRedis(name,newCarts);
				return new Result(true, "添加关注成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加关注失败");
		}
	}

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
			List<Cart> redisCartList = userService.findCartListFromRedis(name);
			if (redisCartList == null) {
				redisCartList = new ArrayList<>();
			}

			//合并cookieCartList与RedisCartList

			String cartListString = CookieUtil.getCookieValue(request, "cartList", true);
			if (StringUtils.isEmpty(cartListString)) {
				//赋以空值,防止CartserviceImpl空指针
				cartListString = "[]";
			}
			List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);

			if (cookieCartList.size() > 0) {
				List<Cart> newestCart = userService.mergeCartList(redisCartList, cookieCartList);
				//最新的购物车保存至redis中
				userService.saveCartListFormRedis(name, newestCart);
				//清除cookie中的购物车
				CookieUtil.deleteCookie(request, response, "cartList");
				return newestCart;
			}
			return redisCartList;
		}

	}
		/**
         * 返回全部列表
         * @return
         */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return userService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */

	/*@RequestMapping("/add")
	public Result add(@Valid @RequestBody TbUser user, BindingResult bindingResult){


		try {
			if(bindingResult.hasErrors()){
				List<FieldError> fieldErrors = bindingResult.getFieldErrors();
				for (FieldError fieldError : fieldErrors) {
					System.out.println("====>"+fieldError.getDefaultMessage());
				}
				return new Result(false,"error");
			}


			*//*boolean flag = userService.checkCode(smsCode, user.getPhone());
			if(flag==false){
				return new Result(false,"验证码错误");
			}*//*
			String password = user.getPassword();
			String s = DigestUtils.md5Hex(password);
			user.setPassword(s);
			user.setCreated(new Date());
			user.setUpdated(new Date());
			userService.add(user);
			return new Result(true,"添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"添加失败");
		}

	}*/

	@RequestMapping("/add/{SmsCode}")
	public Result add(@Valid @RequestBody TbUser user,BindingResult bindingResult,@PathVariable(value = "SmsCode") String smsCode){


		try {
			if(bindingResult.hasErrors()){
				List<FieldError> fieldErrors = bindingResult.getFieldErrors();
				Result result = new Result(false,"error");
				for (FieldError fieldError : fieldErrors) {
					Error error = new Error(fieldError.getField(), fieldError.getDefaultMessage());
					result.getErrorsList().add(error);
					System.out.println("====>"+fieldError.getDefaultMessage());
				}
				return result;
			}

			boolean flag = userService.checkCode(smsCode, user.getPhone());
			if(flag==false){
				return new Result(false,"验证码错误");
			}
			String password = user.getPassword();
			String s = DigestUtils.md5Hex(password);
			user.setPassword(s);
			user.setCreated(new Date());
			user.setUpdated(new Date());
			userService.add(user);
			return new Result(true,"添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"添加失败");
		}

	}

	@RequestMapping("/getCode/{phone}")
	public Result getCode(@PathVariable(value = "phone") String phone){

		try {
			userService.getCode(phone);
			return new Result(true,"请查看手机");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"发送失败");
		}

	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbUser findOne(@PathVariable(value = "id") Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbUser user) {
        return userService.findPage(pageNo, pageSize, user);
    }

    @RequestMapping("/findUnpayOrders")
	public List<TbOrder> findUnpayOrders(){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(userId);
		return userService.findUnpayOrders(userId);
	}


	@RequestMapping("/findMyOrders")
	public List<TbOrder> findMyOrders(){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(userId);
		return userService.findMyOrders(userId);
	}


	@RequestMapping("/findAddress")
	public List<TbAddress> findAddress() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		return userService.findAddress(userId);
	}
	//删除地址
	@RequestMapping("/deleteAddress")
	public Result deleteAddress(@RequestParam(value = "index") Integer index){
		System.out.println(index);
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		int i= userService.deleteAddress(index,userId);
		if(i>=0){
			return new Result(true,"删除成功");
		}else {
			return new Result(false,"删除失败");
		}
	}
	//新增地址
	@RequestMapping("/addAddress")
	public Result addAddress(@RequestBody TbAddress item){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		int i=userService.addAddress(userId,item);
		if(i!=-1){
			return new Result(true,"添加地址成功");
		}else {
			return new Result(false,"添加地址失败");
		}
	}
	//设置地址默认值
	@RequestMapping("/setDefaultAddress")
	public void setDefaultAddress(@RequestParam(value = "index") Integer index){
        System.out.println(index);
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			userService.setDefaultAddress(index,userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//根据id查询地址
	@RequestMapping("/findOneAddress")
	public TbAddress findOneAddress(@RequestParam(value = "id") Long id){
		System.out.println(id);
		return userService.findOneAddress(id);
	}
	//修改地址
	@RequestMapping("/updateAddress")
	public Result updateAddress(@RequestBody TbAddress item){
		int i= userService.updateAddress(item);
		if(i!=-1){
			return new Result(true,"修改成功");
		}else {
			return new Result(false,"修改失败");
		}
	}
	//注册个人信息
	@RequestMapping("/addUserInfo")
	public Result register(@RequestBody TbUser userInfo){
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			userService.register(userInfo,userName);
			return new Result(true,"添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"添加失败");
		}
	}
}
