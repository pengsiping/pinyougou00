package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.IdWorker;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    @Reference
    private SeckillOrderService seckillOrderService;

    private int num = 0;

    /**
     * 生成二维码
     *
     * @return
     */

    @RequestMapping("/createNative")
    public Map createNative() {
        num=0;
        IdWorker idWorker = new IdWorker(0, 1);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //根据用户信息生成订单
        TbSeckillOrder tbSeckillOrder = seckillOrderService.getUserOrderStatus(userId);
        if (tbSeckillOrder != null) {

            double money = tbSeckillOrder.getMoney().doubleValue() * 100;
            long v = (long) money;
            return payService.createNative(tbSeckillOrder.getId() + "", v + "");
        }
        return null;
    }


    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        System.out.println(num);
        try {
            Result result = new Result(false, "支付失败");
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();

            /*查询订单状态*/
            Map<String, String> map = payService.queryPayStatus(out_trade_no);
            System.out.println(map.get("trade_state"));
            if (map == null) {
                result = new Result(false, "支付失败");
            }
            if ("SUCCESS".equals(map.get("trade_state"))) {

                //支付成功,更新数据库,删除预订单
                result = new Result(true, "支付成功");
                seckillOrderService.updateOrderStatus(userId, map.get("transaction_id"));
            } else {
                //支付失败
                num++;
                if (num > 4) {
                    result = new Result(false, "超时");
                    //关闭交易,清除redis,更新商品redis中的数量
                    Map<String, String> map1 = payService.closePay(out_trade_no);
                    if ("SUCCESS".equals(map.get("result_code")) || "ORDERCLOSED".equals(map.get("err_code"))) {
                        //成功关闭订单
                        seckillOrderService.deleteOrder(userId);
                    } else if ("ORDERPAID".equals(map.get("err_code"))) {
                        //关闭的同时,支付成功
                        seckillOrderService.updateOrderStatus(userId, map.get("transaction_id"));
                    } else {
                        //......
                        System.out.println("由于微信端错误");
                    }
                }
            }
            Thread.sleep(3000);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付异常");
        }
    }

   /* @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result=new Result(false,"支付失败");
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        int num=0;
        try {
            while(true){
                *//*查询订单状态*//*
                Map<String,String> map =payService.queryPayStatus(out_trade_no);
                System.out.println(map.get("trade_state"));
                if(map==null){
                    result=new Result(false,"支付失败");
                    break;
                }
                if("SUCCESS".equals(map.get("trade_state"))){

                    //支付成功,更新数据库,删除预订单
                    result=new Result(true,"支付成功");
                    seckillOrderService.updateOrderStatus(userId,map.get("transaction_id"));
                    break;
                } else{
                    //支付失败
                    num++;
                    if(num>4){
                        result= new Result(false,"超时");
                        //关闭交易,清除redis,更新商品redis中的数量
                        Map<String,String> map1 = payService.closePay(out_trade_no);
                        if("SUCCESS".equals(map.get("result_code"))||"ORDERCLOSED".equals(map.get("err_code"))){
                            //成功关闭订单
                            seckillOrderService.deleteOrder(userId);
                        } else if("ORDERPAID".equals(map.get("err_code"))){
                            //关闭的同时,支付成功
                            seckillOrderService.updateOrderStatus(userId,map.get("transaction_id"));
                        } else{
                            //......
                            System.out.println("由于微信端错误");
                        }
                        break;
                    }
                }
                Thread.sleep(3000);
            }
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Result(false,"支付异常");
        }
    }*/
}

