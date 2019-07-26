package com.pinyougou.page.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoodsMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if (list != null) {
            for (MessageExt messageExt : list) {


                byte[] body = messageExt.getBody();

                String s = new String(body);

                MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);

                switch(messageInfo.getMethod()){
                    case 1:{
                        updatePageHtml(messageInfo);
                        break;
                    }

                    case 2:{
                        //必须采用toString转换
                        String context =  messageInfo.getContext().toString();
                        List<TbItem> tbItems = JSON.parseArray(context, TbItem.class);
                        Set<Long> set = new HashSet<>();
                        for (TbItem tbItem : tbItems) {
                            Long goodsId = tbItem.getGoodsId();
                            set.add(goodsId);
                        }

                        for (Long aLong : set) {
                            try {
                                itemPageService.genItemHtml(aLong);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }

                    case 3:{
                        String s1 = messageInfo.getContext().toString();
                        Long[] longs = JSON.parseObject(s1, Long[].class);
                        itemPageService.deleteItemHtml(longs);
                    }
                }


            }
        }

        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }

    private void updatePageHtml(MessageInfo messageInfo) {
        //String context = (String) messageInfo.getContext();  错误
        String context = messageInfo.getContext().toString();

        List<TbItem> tbItems = JSON.parseArray(context, TbItem.class);
        for (TbItem tbItem : tbItems) {
            Long goodsId = tbItem.getGoodsId();
            try {
                itemPageService.genItemHtml(goodsId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }
}
