package com.pinyougou.order.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.OrderTotal;
import com.pinyougou.pojo.TbOrder;

public interface OrderTotalService {
    PageInfo<OrderTotal> selectAll(String startTime,String endTime);

    PageInfo<OrderTotal> findOrder();
}
