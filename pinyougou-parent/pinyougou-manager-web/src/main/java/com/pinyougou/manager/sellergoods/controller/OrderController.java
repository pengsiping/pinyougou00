package com.pinyougou.manager.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.order.service.OrderTotalService;
import com.pinyougou.pojo.OrderTotal;
import com.pinyougou.pojo.TbOrder;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import com.pinyougou.POIUtils;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import entity.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference
	private OrderService orderService;

	@Reference
	private OrderTotalService orderTotalService;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll(){
		return orderService.findAll();
	}


//	@RequestMapping("/orderExport")
//	public ResponseEntity<byte[]> orderExport(){
//		try {
//			List<TbOrder> orders = orderService.findAll();
//			byte[] body = POIUtils.exportExcel(orders).toByteArray();
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Disposition", "attachment; filename=order.xlsx");
//			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//			return new ResponseEntity<>(body, headers, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
//		}
//	}



	@RequestMapping("/findPage")
    public PageInfo<TbOrder> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return orderService.findPage(pageNo, pageSize);
    }

	/**
	 * 增加
	 * @param order
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbOrder order){
		try {
			orderService.add(order);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 修改
	 * @param order
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbOrder order){
		try {
			orderService.update(order);
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
	public TbOrder findOne(@PathVariable(value = "id") Long id){
		return orderService.findOne(id);
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			orderService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}



	@RequestMapping("/search")
    public PageInfo<TbOrder> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbOrder order) {
        return orderService.findPage(pageNo, pageSize, order);
    }

	@RequestMapping("/findOrderTotal")
	public PageInfo<OrderTotal> findOrderTotal( String startTime,
									  String endTime
									  ) {
		return orderTotalService.selectAll(startTime,endTime);
	}

	@RequestMapping("/findOrder")
	public PageInfo<OrderTotal> findAllOrder() {
		return orderTotalService.findOrder();
	}

}
