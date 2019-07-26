package com.pinyougou;

import static org.junit.Assert.assertTrue;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.ResultSet;

/**
 * Unit test for simple App.
 */

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring-producer.xml")
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Autowired
    private DefaultMQProducer producer;


    @Test
    public void sendMessage() throws Exception {
        byte[] s = new String("你好").getBytes();

        //发送消息
        Message msg = new Message("springTopic","TagA",s);

        SendResult sendResult = producer.send(msg);

        System.out.println(sendResult.getMsgId()+">>>>>"+sendResult.getSendStatus()+";>>>>");

        Thread.sleep(10000);


    }
}
