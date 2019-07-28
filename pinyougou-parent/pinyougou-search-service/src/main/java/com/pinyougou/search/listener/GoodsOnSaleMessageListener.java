package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.MessageInfo;

import com.pinyougou.pojo.TbGoods;

import com.pinyougou.search.service.GoodsSearchService;
;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

public class GoodsOnSaleMessageListener  implements MessageListenerConcurrently {

    @Autowired
    private GoodsSearchService goodsSearchService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
       if(list!=null){
           for (MessageExt messageExt : list) {
               //获取消息体
               byte[] body = messageExt.getBody();
               String s = new String(body);
               MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
               switch(messageInfo.getMethod()){
                   case 1:{
                       //
                       String s1 = messageInfo.getContext().toString();
                       List<TbGoods> tbGoodsList = JSON.parseArray(s1, TbGoods.class);
                       goodsSearchService.updateOnSaleGoods(tbGoodsList);
                       break;
                   }

                   case 2:{

                       break;

                   }

                   case 3:{
                       String s1 = messageInfo.getContext().toString();
                       Long[] ids = JSON.parseObject(s1, Long[].class);
                       goodsSearchService.deleteSaleGoods(ids);
                       break;
                   }

                   default:
                       break;
               }
           }
           return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
       }
       return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
