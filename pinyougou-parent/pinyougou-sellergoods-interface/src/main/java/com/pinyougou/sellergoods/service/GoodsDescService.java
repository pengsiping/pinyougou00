package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbGoodsDesc;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsDescService extends CoreService<TbGoodsDesc> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbGoodsDesc> findPage(Integer pageNo,Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbGoodsDesc> findPage(Integer pageNo,Integer pageSize,TbGoodsDesc GoodsDesc);
	
}
