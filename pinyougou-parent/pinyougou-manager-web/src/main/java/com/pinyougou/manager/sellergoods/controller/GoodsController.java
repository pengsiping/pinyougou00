package com.pinyougou.manager.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.pinyougou.MessageInfo;
import com.pinyougou.POIUtils;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemService;
import entity.Goods;
import entity.Result;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	/*@Reference
	private ItemSearchService itemSearchService;

	@Reference
	private ItemPageService itemPageService;*/

	@Reference
	private ItemService itemService;



	@Autowired
	private DefaultMQProducer producer;

	@RequestMapping("/goodsExport")
	public ResponseEntity<byte[]> goodsExport(){
		try {
			List<TbItem> users = itemService.findAll();
			byte[] body = POIUtils.exportExcel(users).toByteArray();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=item.xlsx");
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			return new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
		}
	}

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
	public Result add(@RequestBody TbGoods goods){
		try {
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

	/*@RequestMapping("/updateStatus")

	public Result updateStatus( @RequestParam(value = "status") String status,@RequestBody Long[] ids){
		System.out.println(status+"=="+ids);

		try {
			goodsService.updateStatus(status,ids);
			return new Result(true, "更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新失败");
		}
	}*/

	@RequestMapping("/updateStatus/{status}")

	public Result updateStatus( @PathVariable(value = "status") String status,@RequestBody Long[] ids){
		System.out.println(status+"=="+ids);

		try {
			goodsService.updateStatus(status,ids);
			if("1".equals(status)){
				List<TbItem> tbItemListByIds = goodsService.findTbItemListByIds(ids);
				MessageInfo messageInfo= new MessageInfo("Goods_Topic",
														"Goods_update_tag",
														"updateStatus",
														MessageInfo.METHOD_UPDATE,
														tbItemListByIds);

				SendResult result = producer.send(new Message(messageInfo.getTopic(),
															  messageInfo.getTags(),
															  messageInfo.getKeys(),
															  JSON.toJSONString(messageInfo).getBytes()));

				System.out.println(">>>"+result.getSendStatus());

				/*itemSearchService.updateIndex(tbItemListByIds);

				for (Long id : ids) {
					itemPageService.genItemHtml(id);
				}*/
			}
			return new Result(true, "更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新失败");
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
			//itemSearchService.deleteByIds(ids);
			MessageInfo messageInfo = new MessageInfo("Goods_Topic","Goods_delete_tag","deleteById",MessageInfo.METHOD_DELETE,ids);
			//发送消息
			SendResult result = producer.send(new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes()));
			System.out.println("删除消息发送"+result.getSendStatus());
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
        return goodsService.findPage(pageNo, pageSize, goods);
    }

}
