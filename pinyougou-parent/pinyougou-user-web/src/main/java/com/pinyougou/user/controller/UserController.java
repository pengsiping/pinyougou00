package com.pinyougou.user.controller;
import java.util.Date;
import java.util.List;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.service.UserService;

import entity.Error;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;


import com.github.pagehelper.PageInfo;
import entity.Result;

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
