package com.pinyougou.seckill.task;


import com.pinyougou.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class GoodsTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;


    @Scheduled(cron="0/5 * * * * ?")
    public void pushGoods(){
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");
        criteria.andGreaterThan("stockCount",0);
        Date date = new Date();
        criteria.andLessThan("startTime",date);
        criteria.andGreaterThan("endTime",date);
        //排除已经在redis中的商品
        Set<Long> keys = redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).keys();

        if(keys!=null&&keys.size()>0){
            criteria.andNotIn("id",keys);
        }
        //全部储存到redis中
        List<TbSeckillGoods> tbSeckillGoods = tbSeckillGoodsMapper.selectByExample(example);
        for (TbSeckillGoods tbSeckillGood : tbSeckillGoods) {
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(tbSeckillGood.getId(),tbSeckillGood);
            pushGoodsList(tbSeckillGood);
        }
        System.out.println(new Date());


    }

    private void pushGoodsList(TbSeckillGoods tbSeckillGood) {
        for (int i = 0; i < tbSeckillGood.getStockCount(); i++) {
            redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX+tbSeckillGood.getId()).leftPush(tbSeckillGood.getId());
        }
    }
}
