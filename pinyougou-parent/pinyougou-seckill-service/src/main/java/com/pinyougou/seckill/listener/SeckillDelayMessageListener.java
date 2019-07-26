package com.pinyougou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.MessageInfo;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class SeckillDelayMessageListener implements MessageListenerConcurrently {

    @Autowired
    private TbSeckillOrderMapper tbSeckillOrderMapper;

    @Reference
    private PayService payService;

    @Autowired
    private SeckillOrderService seckillOrderService;


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        if (list!=null){
            for (MessageExt messageExt : list) {
                byte[] body = messageExt.getBody();
                String s = new String(body);
                MessageInfo messageInfo = JSON.parseObject(s, (Type) MessageInfo.class);
                TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) messageInfo.getContext();
                //查询订单状态
                TbSeckillOrder tbSeckillOrder1 = tbSeckillOrderMapper.selectByPrimaryKey(tbSeckillOrder.getId());
                if(tbSeckillOrder1==null){
                    //数据库为空,即表示订单尚未支付,执行关闭订单
                    Map<String,String> map = payService.createNative(tbSeckillOrder1.getId() + "", tbSeckillOrder1.getMoney() + "");
                    if(map!=null){
                        Map map1 = payService.closePay(map.get("out_trade_no"));
                        //订单关闭成功
                        if ("SUCCESS".equals(map1.get("return_code"))||"ORDERCLOSED".equals(map1.get("err_code"))){
                            seckillOrderService.deleteOrder(tbSeckillOrder1.getUserId());
                        } else if("ORDERPAID".equals(map1.get("err_code"))){
                            seckillOrderService.updateOrderStatus(tbSeckillOrder1.getUserId(),map.get("transaction_id"));
                        } else {
                            //系统错误之类
                        }
                    }
                }

            }
           return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
