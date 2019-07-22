package com.pinyougou.user.controller;
import java.util.Date;
import java.util.List;

import com.pinyougou.service.UserService;

import entity.Error;
import org.apache.commons.codec.digest.DigestUtils;
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
	
}
