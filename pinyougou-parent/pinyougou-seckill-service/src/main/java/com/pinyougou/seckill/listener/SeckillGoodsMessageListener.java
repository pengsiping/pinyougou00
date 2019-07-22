package com.pinyougou.seckill.listener;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.MessageInfo;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;


import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillGoodsMessageListener implements MessageListenerConcurrently {

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    TbSeckillGoodsMapper tbSeckillGoodsMapper;

    @Value("${pageDir}")
    private String pageDir;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if (list != null) {
                for (MessageExt messageExt : list) {
                    byte[] body = messageExt.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
                    List<Long> ids = JSON.parseArray(messageInfo.getContext().toString(), Long.class);
                    for (Long id : ids) {
                        gentHtml("item.ftl", id);
                    }

                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }



    }


    public void gentHtml(String templateName, Long id) {
        FileWriter writer=null;
        try {

            //创建configuration对象
            Configuration configuration = configurer.getConfiguration();
            //获取模板对象
            Template template = configuration.getTemplate(templateName);
            //获取结果集
            Map map = new HashMap<>();
            TbSeckillGoods tbSeckillGoods = tbSeckillGoodsMapper.selectByPrimaryKey(id);
            map.put("seckillGoods",tbSeckillGoods);
            //输出
            writer= new FileWriter(new File(pageDir+tbSeckillGoods.getGoodsId()+".html"));

            template.process(map,writer);


        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
