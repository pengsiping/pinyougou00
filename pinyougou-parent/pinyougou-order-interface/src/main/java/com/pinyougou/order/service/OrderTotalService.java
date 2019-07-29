package com.pinyougou.order.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.OrderTotal;

public interface OrderTotalService {
    PageInfo<OrderTotal> selectAll(Integer pageNo, Integer pageSize);
}
