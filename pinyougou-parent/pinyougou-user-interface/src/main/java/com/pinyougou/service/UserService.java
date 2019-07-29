package com.pinyougou.service;
import java.util.List;
import com.pinyougou.pojo.TbUser;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import entity.Cart;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService extends CoreService<TbUser> {

	public List<Cart> addGoodsToCartList(List<Cart> cart, Long itemId, Integer num);

	List<Cart> findCartListFromRedis(String name);

	void saveCartListFormRedis(String name, List<Cart> newCarts);

	List<Cart> mergeCartList(List<Cart> redisCartList, List<Cart> cookieCartList);
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbUser> findPage(Integer pageNo,Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbUser> findPage(Integer pageNo,Integer pageSize,TbUser User);

	void getCode(String phone);

	boolean checkCode(String smsCode,String phone);
}
