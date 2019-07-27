package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.MessageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.Result;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Autowired
	private DefaultMQProducer producer;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getTbGoods().setSellerId(name);
            goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
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
	public Goods findOne(@PathVariable(value = "id") Long id){
		return goodsService.findOne(id);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(name);
		System.out.println("测试"+goods.getSellerId());
		return goodsService.findPage(pageNo, pageSize, goods);
    }





    //商品上下架功能
	@RequestMapping("/updateSaleStatus/{isOnSale}")
	public Result updateSaleStatus(@PathVariable(value = "isOnSale") String isOnSale, @RequestBody Long[] ids){

		try {
			goodsService.updateSaleStatus(isOnSale,ids);
			if("1".equals(isOnSale)){
				List<TbItem> tbItemListByIds = goodsService.findTbItemListByIds(ids);
				MessageInfo messageInfo = new MessageInfo("OnSale_Topic","OnSale_genHtml_tag","Goods_isOnSaleStatus",MessageInfo.METHOD_ADD,tbItemListByIds);
				Message message = new Message(messageInfo.getTopic(),messageInfo.getTags(),messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes());
				SendResult send = producer.send(message);
				System.out.println(send.getSendStatus());
			}

			if("0".equals(isOnSale)){
				MessageInfo messageInfo = new MessageInfo("OnSale_Topic","OnSale_genHtml_tag","Goods_downSaleStatus",MessageInfo.METHOD_DELETE,ids);
				Message message = new Message(messageInfo.getTopic(),messageInfo.getTags(),messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes());
				SendResult send = producer.send(message);
				System.out.println(send.getSendStatus());
			}

			return new Result(true,"已更新上下架信息");
		} catch (Exception e) {
			return new Result(false,e.getMessage());
		}
	}
	
}
