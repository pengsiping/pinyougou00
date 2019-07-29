package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.order.service.DeliverGoodsService;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import entity.Goods;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/deliver")
public class DeliverGoodsController {

	@Reference
	private DeliverGoodsService goodsService;


	/**
	 * 展示已发货，和未发货数据
	 * @param pageNo
	 * @param pageSize
	 * @param order
	 * @return
	 */
	@RequestMapping("/search")
    public PageInfo<TbOrder> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbOrder order) {
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		order.setSellerId(sellerId);
		return goodsService.findPage(pageNo, pageSize, order);
    }
	/**
	 * 修改订单状态，并设置发货时间
	 * @param ids,status
	 * @return
	 */
	@RequestMapping("/update/{status}")
	public Result update(@PathVariable(value = "status") String status,@RequestBody Long[] ids){
		try {
			goodsService.updateStatus(status, ids);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

}
