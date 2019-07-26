package com.pinyougou.mapper;

import com.pinyougou.pojo.TbOrder;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;

public interface TbOrderMapper extends Mapper<TbOrder> {

    BigDecimal getSalesLineChart(@Param("day")String day, @Param("status") String status);
}