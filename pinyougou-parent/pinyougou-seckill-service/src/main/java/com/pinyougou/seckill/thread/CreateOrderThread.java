package com.pinyougou.seckill.thread;

import com.alibaba.fastjson.JSON;
import com.pinyougou.IdWorker;
import com.pinyougou.MessageInfo;
import com.pinyougou.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.pojo.SeckillStatus;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

public class CreateOrderThread {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    /*@Autowired
    private TbSeckillOrderMapper tbSeckillOrderMapper;*/

    /*多线程执行下单操作,异步方法*/
    @Async
    public void handleOrder(){
        try {
            System.out.println("模拟处理订单开始----"+Thread.currentThread().getName());
            Thread.sleep(30000);
            System.out.println("模拟处理订单结束 ----"+Thread.currentThread().getName());
            SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).rightPop();

            if(seckillStatus!=null){

                TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillStatus.getGoodsId());

                tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()-1);
                System.out.println(tbSeckillGoods.getStockCount());
                //商品数量为0,更新数据库,并删除redis内的数据
                if(tbSeckillGoods.getStockCount()<=0){
                    tbSeckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods); //更新数据库
                    redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(seckillStatus.getGoodsId());
                }
                //商品数量不为0,更新redis内数据
                redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillStatus.getGoodsId(),tbSeckillGoods);

                //生成秒杀订单
                TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
                //創建訂單號
                long orderId = idWorker.nextId();


                tbSeckillOrder.setId(orderId);
                tbSeckillOrder.setSeckillId(seckillStatus.getGoodsId());
                tbSeckillOrder.setMoney(tbSeckillGoods.getCostPrice());
                tbSeckillOrder.setUserId(seckillStatus.getUserId());
                tbSeckillOrder.setSellerId(tbSeckillGoods.getSellerId());
                tbSeckillOrder.setCreateTime(new Date());
                tbSeckillOrder.setStatus("0");  //未支付狀態
                System.out.println("1111");
                // tbSeckillOrderMapper.insert(tbSeckillOrder);
                redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).put(seckillStatus.getUserId(),tbSeckillOrder);

                //移除排队标识,下单成功
                redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).delete(seckillStatus.getUserId());

                sendMessage(tbSeckillOrder);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Autowired
    private DefaultMQProducer producer;

    private void sendMessage(TbSeckillOrder tbSeckillOrder) {
        MessageInfo messageInfo = new MessageInfo("TOPIC_SECKILL_DELAY","TAG_SECKILL_DELAY","handleOrder_DELAY",MessageInfo.METHOD_UPDATE,tbSeckillOrder);

        Message message = new Message(messageInfo.getTopic(),messageInfo.getTags(),messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes());

        try {
            //半小时后发送消息
            message.setDelayTimeLevel(9);
            SendResult send = producer.send(message);
            System.out.println(send.getSendStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
