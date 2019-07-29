package com.pinyougou.mapper;

import com.pinyougou.pojo.OrderTotal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderTotalMapper {

    public List<OrderTotal> selectAll();
    public List<OrderTotal> selectByTime(@Param("startTime")String startTime, @Param("endTime") String endTime);
}
