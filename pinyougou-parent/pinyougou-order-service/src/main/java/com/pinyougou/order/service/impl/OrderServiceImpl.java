package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.IdWorker;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.*;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.*;
import entity.Cart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;
import org.apache.commons.beanutils.ConvertUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder> implements OrderService {


    private TbOrderMapper orderMapper;

    @Autowired
    public OrderServiceImpl(TbOrderMapper orderMapper) {
        super(orderMapper, TbOrder.class);
        this.orderMapper = orderMapper;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbPayLogMapper tbPayLogMapper;
    @Autowired
    private TbSellerMapper sellerMapper;





    @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbOrder> all = orderMapper.selectAll();
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);

        //序列化再反序列化
//        String s = JSON.toJSONString(info);
//        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return info;
    }


    @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder order) {
        PageHelper.startPage(pageNo, pageSize);
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (order != null) {
            if (StringUtils.isNotBlank(order.getPaymentType())) {
                criteria.andLike("paymentType", "%" + order.getPaymentType() + "%");
                //criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
            }
            if (StringUtils.isNotBlank(order.getPostFee())) {
                criteria.andLike("postFee", "%" + order.getPostFee() + "%");
                //criteria.andPostFeeLike("%"+order.getPostFee()+"%");
            }
            if (StringUtils.isNotBlank(order.getStatus())) {
                criteria.andLike("status", "%" + order.getStatus() + "%");
                //criteria.andStatusLike("%"+order.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(order.getShippingName())) {
                criteria.andLike("shippingName", "%" + order.getShippingName() + "%");
                //criteria.andShippingNameLike("%"+order.getShippingName()+"%");
            }
            if (StringUtils.isNotBlank(order.getShippingCode())) {
                criteria.andLike("shippingCode", "%" + order.getShippingCode() + "%");
                //criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
            }
            if (StringUtils.isNotBlank(order.getUserId())) {
                criteria.andLike("userId", "%" + order.getUserId() + "%");
                //criteria.andUserIdLike("%"+order.getUserId()+"%");
            }
            if (StringUtils.isNotBlank(order.getBuyerMessage())) {
                criteria.andLike("buyerMessage", "%" + order.getBuyerMessage() + "%");
                //criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
            }
            if (StringUtils.isNotBlank(order.getBuyerNick())) {
                criteria.andLike("buyerNick", "%" + order.getBuyerNick() + "%");
                //criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
            }
            if (StringUtils.isNotBlank(order.getBuyerRate())) {
                criteria.andLike("buyerRate", "%" + order.getBuyerRate() + "%");
                //criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiverAreaName())) {
                criteria.andLike("receiverAreaName", "%" + order.getReceiverAreaName() + "%");
                //criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiverMobile())) {
                criteria.andLike("receiverMobile", "%" + order.getReceiverMobile() + "%");
                //criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiverZipCode())) {
                criteria.andLike("receiverZipCode", "%" + order.getReceiverZipCode() + "%");
                //criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
            }
            if (StringUtils.isNotBlank(order.getReceiver())) {
                criteria.andLike("receiver", "%" + order.getReceiver() + "%");
                //criteria.andReceiverLike("%"+order.getReceiver()+"%");
            }
            if (StringUtils.isNotBlank(order.getInvoiceType())) {
                criteria.andLike("invoiceType", "%" + order.getInvoiceType() + "%");
                //criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
            }
            if (StringUtils.isNotBlank(order.getSourceType())) {
                criteria.andLike("sourceType", "%" + order.getSourceType() + "%");
                //criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
            }
            if (StringUtils.isNotBlank(order.getSellerId())) {
                criteria.andEqualTo("sellerId",order.getSellerId());
                //criteria.andSellerIdLike("%"+order.getSellerId()+"%");
            }

        }
        List<TbOrder> all = orderMapper.selectByExample(example);
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);
//        //序列化再反序列化
//        String s = JSON.toJSONString(info);
//        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return info;
    }
    /*
     * 获取日志信息
     * @param userId
     * @return
     */
    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {

       return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);

    }



    //更新交易日志信息
    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        TbPayLog tbPayLog = tbPayLogMapper.selectByPrimaryKey(out_trade_no);
        tbPayLog.setTransactionId(transaction_id);
        tbPayLog.setTradeState("1");
        tbPayLog.setPayTime(new Date());
        tbPayLogMapper.updateByPrimaryKey(tbPayLog);

        //修改订单状态
        String orderList = tbPayLog.getOrderList();
        String[] orderIds = orderList.split(",");
        for (String orderId : orderIds) {
            TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            if(order!=null){
                order.setStatus("2");
                order.setUpdateTime(new Date());
                System.out.println(order);
                System.out.println(order.getUpdateTime()+"===="+order.getPaymentTime());
                order.setPaymentTime(order.getUpdateTime());
                //更新订单信息
                orderMapper.updateByPrimaryKey(order);
            }

        }
        redisTemplate.boundHashOps("payLog").delete(tbPayLog.getUserId());
    }

    @Override
    public void recoverRedisCartList(String userId, String out_trade_no) {
        Long[] orderIds=null;
        List<TbOrderItem> orderItemList=null;
        String sellerId=null;
        List<Cart> cartList = new ArrayList<>();
        TbPayLog payLog = new TbPayLog();
        payLog.setOutTradeNo(out_trade_no);
        List<TbPayLog> tbPayLogList = tbPayLogMapper.select(payLog);
        if(tbPayLogList!=null) {
            for (TbPayLog tbPayLog : tbPayLogList) {
                Cart cart= new Cart();
                String orderList = tbPayLog.getOrderList();//"23,32"
                String[] strArray = orderList.split(",");//"[23,32]"
                 orderIds = (Long[]) ConvertUtils.convert(strArray,Long.class);
                if(orderIds!=null) {
                    for (Long orderId : orderIds) {
                        //设置orderItemList
                        TbOrderItem tbOrderItem = new TbOrderItem();
                        tbOrderItem.setOrderId(orderId);
                        orderItemList = tbOrderItemMapper.select(tbOrderItem);
                        cart.setOrderItemList(orderItemList);

                        TbOrder tbOrder = orderMapper.selectByPrimaryKey(orderId);
                        for (TbOrderItem orderItem : orderItemList) {
                            sellerId = orderItem.getSellerId();
                            cart.setSellerId(sellerId);
                        }
                        TbSeller tbSeller = new TbSeller();
                        tbSeller.setSellerId(sellerId);
                        List<TbSeller> tbSellerList = sellerMapper.select(tbSeller);
                        if(tbSellerList!=null) {
                            for (TbSeller seller : tbSellerList) {
                                //店铺名称
                                String nickName = seller.getNickName();
                                cart.setSellerName(nickName);
                            }
                        }

                    }
                }
                cartList.add(cart);
            }
        }
        redisTemplate.boundHashOps("REDIS_CARTLIST").put(userId,cartList);
        //删除订单表数据，删除订单明细表数据,支付日志表
        for (Long orderId : orderIds) {
            orderMapper.deleteByPrimaryKey(orderId);
        }
        for (TbOrderItem tbOrderItem : orderItemList) {
            tbOrderItemMapper.delete(tbOrderItem);
        }
        TbPayLog tbPayLog = new TbPayLog();
        tbPayLog.setOutTradeNo(out_trade_no);
        tbPayLogMapper.delete(tbPayLog);

        throw new RuntimeException("超时");
    }

    @Override
    public List<BigDecimal> getSalesLineChart(List<String> daysList) {
        String status= "1";
        List<BigDecimal> moneyList = new ArrayList<>();
        for (String day : daysList) {
          BigDecimal  dayMoney = orderMapper.getSalesLineChart(day,status);
          moneyList.add(dayMoney);
        }
        return moneyList;
    }

    @Override
    public List<TbOrder> findAllSales() {
       List<TbOrder> list =  orderMapper.findAllSales();
       return list;
    }

    @Override
    public List<TbOrder> findAllSales(String startTime, String endTime) {
        List<TbOrder> list =  orderMapper.findOnTimeSales(startTime,endTime);
        return list;
    }

    @Override
    public PageInfo<TbOrder> findAllOrder() {
        PageHelper.startPage(1, 10);
        List<TbOrder> list =  orderMapper.findAllSales();
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(list);
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

    @Override
    public PageInfo<TbOrder> findOrderInSomeTime(String startTime, String endTime) {
        PageHelper.startPage(1, 10);
        List<TbOrder> list =  orderMapper.findOnTimeSales(startTime,endTime);
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(list);
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public void add(TbOrder record) {
        //获取该用户购物车数据
        List<Cart> redisCartList = (List<Cart>) redisTemplate.boundHashOps("REDIS_CARTLIST").get(record.getUserId());
        List<Long> orderList= new ArrayList<>();
        double totalMoney=0;
        for (Cart cart : redisCartList) {
            //每一个SellerId代表一个Cart
            //设置orderId
            //新建订单
            TbOrder order = new TbOrder();
            long orderId = idWorker.nextId();
            order.setOrderId(orderId);
            orderList.add(orderId);
            order.setPaymentType(record.getPaymentType());  //设置支付方式
            order.setStatus("1");  //支付状态 1代表未支付
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            order.setUserId(record.getUserId());
            order.setReceiverAreaName(record.getReceiverAreaName());
            order.setReceiver(record.getReceiver());
            order.setSellerId(record.getSellerId());
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            double num = 0;
            for (TbOrderItem tbOrderItem : orderItemList) {
                //每一个SKU生成一个订单
                long tbOrderItemId = idWorker.nextId();
                tbOrderItem.setId(tbOrderItemId); //设置sku的小订单号
                tbOrderItem.setOrderId(orderId);  //设置大订单号
                Long itemId = tbOrderItem.getItemId();
                //设置GoodsId
                TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
                tbOrderItem.setGoodsId(tbItem.getGoodsId());
                tbOrderItemMapper.insert(tbOrderItem);
                num += tbOrderItem.getTotalFee().doubleValue();
            }
            totalMoney+=num;
            order.setPayment(new BigDecimal(num));
            orderMapper.insert(order);
        }
        redisTemplate.boundHashOps("REDIS_CARTLIST").delete(record.getUserId());
        TbPayLog tbPayLog = new TbPayLog();
        String out_trade_no = idWorker.nextId()+""; //支付订单号
        tbPayLog.setOutTradeNo(out_trade_no);
        tbPayLog.setCreateTime(new Date());
        tbPayLog.setTotalFee((long) (totalMoney*100));
        tbPayLog.setUserId(record.getUserId());
        tbPayLog.setTradeState("0");
        tbPayLog.setPayType(record.getPaymentType());
        String ids = orderList.toString().replace("[","").replace("]","");
        tbPayLog.setOrderList(ids);

        tbPayLogMapper.insert(tbPayLog);
        redisTemplate.boundHashOps("payLog").put(tbPayLog.getUserId(),tbPayLog);

    }
}
