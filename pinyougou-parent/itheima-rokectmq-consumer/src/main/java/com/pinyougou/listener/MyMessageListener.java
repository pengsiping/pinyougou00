package com.pinyougou.listener;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class MyMessageListener implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.println("Receive new Message:"+Thread.currentThread().getName());

        if(list!=null){
            for (MessageExt messageExt : list) {
                System.out.println("msgID"+messageExt.getMsgId()+">>>>>"+new String(new String(messageExt.getBody()) +">>>>>"+messageExt.getTags()));

            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
