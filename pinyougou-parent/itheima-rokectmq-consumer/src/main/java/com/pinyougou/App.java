package com.pinyougou;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException, MQBrokerException {

        //1.创建生产者 对象 并指定组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_group1");

        //2.设置 nameserver的地址
        consumer.setNamesrvAddr("127.0.0.1:9876");

        //3.设置消费模式 默认就是集群模式
        consumer.setMessageModel(MessageModel.CLUSTERING);

        //4.设置(订阅)消费主题,并执行消费的 标签  * 表示指定所有标签
        consumer.subscribe("TopicTest","*");


        //4.设置监听器 同时消费 不需要按住顺序来消费

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                if(list!=null){
                    for (MessageExt messageExt : list) {
                        byte[] body = messageExt.getBody();
                        String message = new String(body);
                        System.out.println(message+"%s%n"+messageExt.getTopic()+"%s%n"+messageExt.getTags());
                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }

                //消费失败
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });

        //发布开始消费
        consumer.start();

    }
}
