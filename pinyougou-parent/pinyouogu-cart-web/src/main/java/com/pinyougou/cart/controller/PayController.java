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
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        IdWorker idWorker = new IdWorker(0,1);
        //根据用户信息生成订单
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(userId);
        if (tbPayLog!=null){
            return payService.createNative(tbPayLog.getOutTradeNo()+"",tbPayLog.getTotalFee()+"");
        }
        return null;
    }


    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
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
            //5分钟超时
            if(num>20){ //100
               //关闭支付接口
                Map map1 = payService.closePay(out_trade_no);
                if("ORDERPAID".equals(map1.get("err_code"))){
                   //已经支付，则修改状态
                    orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));
                    //订单关闭成功，或者错误码为订单已关闭
                }else if("SUCCESS".equals(map.get("result_code"))||"ORDERCLOSED".equals(map1.get("err_code"))) {
                //恢复redis中购物车的数据,并删除订单表数据，删除订单明细表数据,支付日志表
                    try {
                        orderService.recoverRedisCartList(userId, out_trade_no);
                    } catch (Exception e) {
                        e.printStackTrace();
                        result = new Result(false,e.getMessage());
                    }

                }else{
                    System.out.println("由于微信端错误");
                }
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

