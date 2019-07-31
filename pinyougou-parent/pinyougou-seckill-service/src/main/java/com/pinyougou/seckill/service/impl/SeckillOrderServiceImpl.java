package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.IdWorker;
import com.pinyougou.SysConstants;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.pojo.SeckillStatus;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.seckill.service.impl.SeckillGoodsServiceImpl;
import com.pinyougou.seckill.thread.CreateOrderThread;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<TbSeckillOrder> implements SeckillOrderService {

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private CreateOrderThread createOrderThread;


    @Autowired
    public SeckillOrderServiceImpl(TbSeckillOrderMapper seckillOrderMapper) {
        super(seckillOrderMapper, TbSeckillOrder.class);
        this.seckillOrderMapper = seckillOrderMapper;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbSeckillOrder> all = seckillOrderMapper.selectAll();
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getId()!=null) {
                criteria.andLike("id", "%" + seckillOrder.getId().toString() + "%");
                //criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
            }if (StringUtils.isNotBlank(seckillOrder.getUserId())) {
                criteria.andLike("userId", "%" + seckillOrder.getUserId() + "%");
                //criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getSellerId())) {
                criteria.andLike("sellerId", "%" + seckillOrder.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getStatus())) {
                criteria.andLike("status", "%" + seckillOrder.getStatus() + "%");
                //criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiverAddress())) {
                criteria.andLike("receiverAddress", "%" + seckillOrder.getReceiverAddress() + "%");
                //criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiverMobile())) {
                criteria.andLike("receiverMobile", "%" + seckillOrder.getReceiverMobile() + "%");
                //criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiver())) {
                criteria.andLike("receiver", "%" + seckillOrder.getReceiver() + "%");
                //criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getTransactionId())) {
                criteria.andLike("transactionId", "%" + seckillOrder.getTransactionId() + "%");
                //criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
            }

        }
        List<TbSeckillOrder> all = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);
        //序列化再反序列化
        //String s = JSON.toJSONString(info);
        //PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        PageInfo<TbSeckillOrder> pageInfo = info;

        return pageInfo;
    }

    @Override
    public void submitOrder(Long seckillId, String userId) {


        if (redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).get(userId) != null) {
            throw new RuntimeException("已在队列中");
        }

        Object o = redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
        if (o != null) {
            throw new RuntimeException("你有未支付的订单,请先支付");
        }

        Long goodsId = (Long) redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + seckillId).rightPop();
        if (goodsId == null) {
            throw new RuntimeException("商品已被搶光");
        }

        //进入下单排队队列中
        redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).leftPush(
                new SeckillStatus(userId, seckillId, SeckillStatus.SECKILL_queuing)
        );

        //用户进入排队.并设置标识
        redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).put(userId, seckillId);

        //多线程下单
        createOrderThread.handleOrder();


		/*
		//從redis 內獲取秒殺商品
		TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillId);
		//秒殺商品沒了,redis中即不存在或剩餘數量為0,提示商品已搶光
		*//*if(tbSeckillGoods==null ||tbSeckillGoods.getStockCount()<=0){
			throw new RuntimeException("商品已被搶光");
		}*//*
		//秒殺商品還有, 1.原有的數量更新 2.生成秒殺訂單
		tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()-1);
		//更新redis內的數據:1.如果stockCount=0,清空該商品,更新數據庫信息2.不等於0則更新redis及數據庫
		if(tbSeckillGoods.getStockCount()==0){
			tbSeckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
			redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(seckillId);
		}
		//tbSeckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
		redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId,tbSeckillGoods);

		//生成秒殺訂單

		//創建訂單號
		long orderId = idWorker.nextId();

		TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
		tbSeckillOrder.setId(orderId);
		tbSeckillOrder.setSeckillId(seckillId);
		tbSeckillOrder.setMoney(tbSeckillGoods.getCostPrice());
		tbSeckillOrder.setUserId(userId);
		tbSeckillOrder.setSellerId(tbSeckillGoods.getSellerId());
		tbSeckillOrder.setCreateTime(new Date());
		tbSeckillOrder.setStatus("0");  //未支付狀態

		seckillOrderMapper.insert(tbSeckillOrder);
		redisTemplate.boundHashOps(SysConstants.SEC_KILL_USER_ORDER_LIST).put(userId,tbSeckillOrder);
*/

    }

    @Override
    public TbSeckillOrder getUserOrderStatus(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
    }

    @Override
    public void updateOrderStatus(String userId, String transaction_id) {
        TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) getUserOrderStatus(userId);
        if (tbSeckillOrder != null) {
            //更新支付状态,产品数量
            tbSeckillOrder.setStatus("1");  //设置支付状态
            tbSeckillOrder.setPayTime(new Date());  //设置支付时间
            tbSeckillOrder.setTransactionId(transaction_id); //设置交易流水号
            seckillOrderMapper.insert(tbSeckillOrder);  //更新至数据库
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);  //删除预订单
        }


    }

    @Override
    public void deleteOrder(String userId) {

        //获取redis中的预订单
        TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) getUserOrderStatus(userId);
        if(tbSeckillOrder==null){
            System.out.println("没有该订单");
            return ;
        }
        Long seckillId = tbSeckillOrder.getSeckillId();
        //获取原来redis中的商品
        TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillId);
        if (tbSeckillGoods != null) {
            tbSeckillGoods.setNum(tbSeckillGoods.getStockCount() + 1);
            //重新更新redis数据
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId, tbSeckillGoods);
        } else {
            //商品数量为0时,redis中没有该商品,更新数据库
            TbSeckillGoods tbSeckillGoods1 = tbSeckillGoodsMapper.selectByPrimaryKey(userId);
            tbSeckillGoods1.setStockCount(1);
            //更新数据库
            tbSeckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods1);
        }

        //商品队列加入元素
        redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX).leftPush(seckillId);

        //删除订单
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);


    }


}
