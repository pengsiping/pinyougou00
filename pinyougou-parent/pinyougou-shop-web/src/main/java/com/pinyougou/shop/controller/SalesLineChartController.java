package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author:yl
 * @Date:2019/7/25 10:31
 * @Version 1.0
 */
@RestController
@RequestMapping("/sale")
public class SalesLineChartController {
    @Reference
    private OrderService orderService;
    @RequestMapping("/getSalesLineChart")
    public Map<String,Object> getSalesLineChart(String startDate,String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
            List<String> daysList = new ArrayList<>();
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            while(tempStart.before(tempEnd)||tempStart.equals(tempEnd)) {
                daysList.add(sdf.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR,1);
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("oneday",daysList);

            List<BigDecimal> moneyList = orderService.getSalesLineChart(daysList);
             map.put("money",moneyList);
              return map;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
