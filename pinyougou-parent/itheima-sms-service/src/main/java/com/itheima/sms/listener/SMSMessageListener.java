package com.itheima.sms.listener;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.sms.util.SmsUtil;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class SMSMessageListener implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if(list!=null){
                for (MessageExt messageExt : list) {

                        byte[] body = messageExt.getBody();
                        String s = new String(body);
                        Map<String,String> map = JSON.parseObject(s, Map.class);
                        System.out.println("=====>"+map);
                        SmsUtil.sendSms(map);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (ClientException e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }

    }
}
