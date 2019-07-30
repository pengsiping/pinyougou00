package com.pinyougou.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbUser;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService extends CoreService<TbUser> {
	
	
	
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
	/**
	 * 查询未付款的订单
	 * @param userId
	 * @return
	 */
	List<TbOrder> findUnpayOrders(String userId);


    List<TbOrder> findMyOrders(String userId);



    List<TbAddress> findAddress(String userId);

	int deleteAddress(Integer index,String userId);

	int addAddress(String userId,TbAddress item);

	void setDefaultAddress(Integer index,String userId);

	TbAddress findOneAddress(Long id);

	int updateAddress(TbAddress item);

	void register(TbUser userInfo,String userName);

    void findMyFootprint(Long goodsId, String userId);

	List<Map<String, Object>> findAllFootprint(String userId);
}
