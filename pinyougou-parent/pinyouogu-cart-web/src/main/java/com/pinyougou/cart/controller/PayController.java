package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.IdWorker;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.PayService;

import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    @Reference
    private OrderService orderService;

    /**
     * 生成二维码
     * @return
     */

    @RequestMapping("/createNative")
    public Map createNative(){
        IdWorker idWorker = new IdWorker(0,1);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //根据用户信息生成订单
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(userId);
        if (tbPayLog!=null){
            return payService.createNative(tbPayLog.getOutTradeNo()+"",tbPayLog.getTotalFee()+"");
        }
        return null;
    }


    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result=null;
        int num=0;
        while(true){
            Map<String,String> map =payService.queryPayStatus(out_trade_no);
            System.out.println(map.get("trade_state"));
            if(map==null){
                result=new Result(false,"支付失败");
                break;

            }
            if("SUCCESS".equals(map.get("trade_state"))){
                result=new Result(true,"支付成功");
                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));
                break;
            }
            num++;
            if(num>100){
                result= new Result(false,"超时");
                break;
            }

            try {
                Thread.sleep(3000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        return result;
    }
}

