package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.OrderTotalMapper;
import com.pinyougou.order.service.OrderTotalService;
import com.pinyougou.pojo.OrderTotal;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class OrderTotalServiceImpl implements OrderTotalService {

    @Autowired
    private OrderTotalMapper orderTotalMapper;
    @Override
    public PageInfo<OrderTotal> selectAll(String startTime,String endTime) {
        PageHelper.startPage(1, 8);
        List<OrderTotal> all = orderTotalMapper.selectByTime(startTime,endTime);
        PageInfo<OrderTotal> info = new PageInfo<OrderTotal>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<OrderTotal> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

    @Override
    public PageInfo<OrderTotal> findOrder() {
        PageHelper.startPage(1, 8);
        List<OrderTotal> all = orderTotalMapper.selectAll();
        PageInfo<OrderTotal> info = new PageInfo<OrderTotal>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<OrderTotal> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }
}
