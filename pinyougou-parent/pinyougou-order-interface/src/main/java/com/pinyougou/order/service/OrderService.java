package com.pinyougou.order.service;

import java.math.BigDecimal;
import java.util.List;
import com.pinyougou.pojo.TbOrder;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbPayLog;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService extends CoreService<TbOrder> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbOrder> findPage(Integer pageNo,Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbOrder> findPage(Integer pageNo,Integer pageSize,TbOrder Order);

    TbPayLog searchPayLogFromRedis(String userId);

	void updateOrderStatus(String out_trade_no, String transaction_id);

    void recoverRedisCartList(String userId, String out_trade_no);

   List<BigDecimal> getSalesLineChart(List<String> daysList);


	List<TbOrder> findAllSales();
	List<TbOrder> findAllSales(String startTime,String endTime);


	PageInfo<TbOrder> findAllOrder();

	PageInfo<TbOrder> findOrderInSomeTime(String startTime,String endTime);
}
