package com.pinyougou.seckill.task;

import com.pinyougou.mapper.TbAnalysePVMapper;
import com.pinyougou.pojo.TbAnalysePV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class UserAnalyse {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbAnalysePVMapper pvMapper;

    @Scheduled(cron="0 0 * * * ? ")
    public void UserCount(){
        RedisAtomicLong count = new RedisAtomicLong("portalCount", redisTemplate.getConnectionFactory());
        long num = count.get();
        count.expire(0, TimeUnit.SECONDS);
        Date date = new Date();
        Example example = new Example(TbAnalysePV.class);
        example.createCriteria().andIsNull("endTime");
        List<TbAnalysePV> oldPVs = pvMapper.selectByExample(example);
        if(oldPVs!=null&&oldPVs.size()!=0){
            TbAnalysePV oldPV = oldPVs.get(0);
            oldPV.setEndTime(date);
            oldPV.setNum(num);
            pvMapper.updateByPrimaryKey(oldPV);
        }
        TbAnalysePV newPV = new TbAnalysePV();
        newPV.setStartTime(date);
        pvMapper.insert(newPV);
    }
}
