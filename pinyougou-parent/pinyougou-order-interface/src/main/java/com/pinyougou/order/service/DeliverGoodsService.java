package com.pinyougou.order.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface DeliverGoodsService extends CoreService<TbOrder> {





	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder Order);

	/**
	 * 修改状态和设置发货时间
	 * @param ids
	 */
	void updateStatus(String status,Long[] ids);
}
