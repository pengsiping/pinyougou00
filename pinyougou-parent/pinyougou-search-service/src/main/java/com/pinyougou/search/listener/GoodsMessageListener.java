package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.MessageInfo;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.dao.ItemSearchDao;

import com.pinyougou.search.service.ItemSearchService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

public class GoodsMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemSearchService itemSearchService;


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if(list!=null){
            for (MessageExt messageExt : list) {
                byte[] body = messageExt.getBody();

                String s = new String(body);

                MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);

                switch (messageInfo.getMethod()){
                    case 1:{
                        //新增

                        String context = messageInfo.getContext().toString();

                        List<TbItem> tbItems = JSON.parseArray(context, TbItem.class);
                        itemSearchService.updateIndex(tbItems);
                        break;
                    }

                    case 2:{
                        //更新
                        String context = messageInfo.getContext().toString();

                        List<TbItem> tbItems = JSON.parseArray(context, TbItem.class);
                        itemSearchService.updateIndex(tbItems);
                        break;
                    }

                    case 3:{

                        //删除 ?
                        String context = messageInfo.getContext().toString();
                        Long[] longs = JSON.parseObject(context, Long[].class);
                        itemSearchService.deleteByIds(longs);
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
