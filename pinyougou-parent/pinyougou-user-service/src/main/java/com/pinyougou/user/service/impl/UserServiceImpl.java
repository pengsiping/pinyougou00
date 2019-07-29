package com.pinyougou.user.service.impl;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.service.UserService;
import entity.Cart;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;  




/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser>  implements UserService {

	
	private TbUserMapper userMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbItemMapper tbItemMapper;

	@Autowired
	private DefaultMQProducer producer;

	@Value("${SignName}")
	private String Sign_Name;

	@Value("${TemplateCode}")
	private String TemplateCode;



	@Autowired
	public UserServiceImpl(TbUserMapper userMapper) {
		super(userMapper, TbUser.class);
		this.userMapper=userMapper;
	}


	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		//找到添加购物车的商品信息
		TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
		String sellerId = tbItem.getSellerId();
		Cart c = findCartBySellerId(sellerId, cartList);

		//判断之前购物车是否已添加该商品
		if (c == null) {

			c = new Cart();

			//设置Cart信息:sellerID,sellerName,orderItemList
			c.setSellerId(tbItem.getSellerId());
			c.setSellerName(tbItem.getSeller());

			//设置购物车明细
			List<TbOrderItem> list = new ArrayList<>();
			TbOrderItem tbOrderItem = new TbOrderItem();
			//设置id
			tbOrderItem.setItemId(tbItem.getId());
			tbOrderItem.setGoodsId(tbItem.getGoodsId());
			tbOrderItem.setTitle(tbItem.getTitle());
			tbOrderItem.setPrice(tbItem.getPrice());
			tbOrderItem.setSellerId(sellerId);

			tbOrderItem.setNum(num);
			double v = num * tbItem.getPrice().doubleValue();
			tbOrderItem.setTotalFee(new BigDecimal(v));
			tbOrderItem.setPicPath(tbItem.getImage());
			list.add(tbOrderItem);
			c.setOrderItemList(list);
			cartList.add(c);
		} else {
			//cart不为空
			List<TbOrderItem> orderItemList = c.getOrderItemList();
			//根据itemID判断单个商品是否添加到购物车
			TbOrderItem tbOrderItem = findOrderItemByItemId(itemId, orderItemList);
			if (tbOrderItem == null) {
				tbOrderItem = new TbOrderItem();
				//商品列表没有该项
				tbOrderItem.setItemId(tbItem.getId());
				tbOrderItem.setGoodsId(tbItem.getGoodsId());
				tbOrderItem.setTitle(tbItem.getTitle());
				tbOrderItem.setPrice(tbItem.getPrice());
				tbOrderItem.setSellerId(sellerId);
				tbOrderItem.setNum(num);
				double v = num * tbItem.getPrice().doubleValue();
				tbOrderItem.setTotalFee(new BigDecimal(v));
				tbOrderItem.setPicPath(tbItem.getImage());
				orderItemList.add(tbOrderItem);
			} else {
				//商品列表有该项
				Integer num1 = tbOrderItem.getNum() + num;
				tbOrderItem.setNum(num1);
				double v = num1 * tbItem.getPrice().doubleValue();
				tbOrderItem.setTotalFee(new BigDecimal(v));

				if (num1 < 1) {
					orderItemList.remove(tbOrderItem);
				}
				if (orderItemList.size() == 0) {
					cartList.remove(c);
				}

			}

		}

		return cartList;
	}

	@Override
	public List<Cart> findCartListFromRedis(String name) {
		List<Cart> redisCartList = (List<Cart>) redisTemplate.boundHashOps("REDIS_USERFAVORITESLIST").get(name);
		if(redisCartList==null){
			redisCartList=new ArrayList<>();
		}
		return redisCartList;
	}

	@Override
	public void saveCartListFormRedis(String name, List<Cart> newCarts) {
		redisTemplate.boundHashOps("REDIS_USERFAVORITESLIST").put(name, newCarts);
	}

	/**
	 * 合并cookie与redis购物车
	 *
	 * @param redisCartList
	 * @param cookieCartList
	 * @return
	 */
	@Override
	public List<Cart> mergeCartList(List<Cart> redisCartList, List<Cart> cookieCartList) {
		for (Cart cart : cookieCartList) {
			for (TbOrderItem tbOrderItem : cart.getOrderItemList()) {
				redisCartList = addGoodsToCartList(redisCartList, tbOrderItem.getItemId(), tbOrderItem.getNum());
			}
		}
		return redisCartList;
	}


	private TbOrderItem findOrderItemByItemId(Long itemId, List<TbOrderItem> orderItemList) {
		for (TbOrderItem tbOrderItem : orderItemList) {
			if (itemId.equals(tbOrderItem.getItemId())) {
				return tbOrderItem;
			}

		}
		return null;
	}


	private Cart findCartBySellerId(String sellerId, List<Cart> cartList) {
		if (cartList != null) {
			for (Cart cart : cartList) {
				if (cart.getSellerId().equals(sellerId)) {
					return cart;
				}
			}
		}
		return null;
	}

	@Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbUser> all = userMapper.selectAll();
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser user) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();

        if(user!=null){			
						if(StringUtils.isNotBlank(user.getUsername())){
				criteria.andLike("username","%"+user.getUsername()+"%");
				//criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(StringUtils.isNotBlank(user.getPassword())){
				criteria.andLike("password","%"+user.getPassword()+"%");
				//criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(StringUtils.isNotBlank(user.getPhone())){
				criteria.andLike("phone","%"+user.getPhone()+"%");
				//criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(StringUtils.isNotBlank(user.getEmail())){
				criteria.andLike("email","%"+user.getEmail()+"%");
				//criteria.andEmailLike("%"+user.getEmail()+"%");
			}
			if(StringUtils.isNotBlank(user.getSourceType())){
				criteria.andLike("sourceType","%"+user.getSourceType()+"%");
				//criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}
			if(StringUtils.isNotBlank(user.getNickName())){
				criteria.andLike("nickName","%"+user.getNickName()+"%");
				//criteria.andNickNameLike("%"+user.getNickName()+"%");
			}
			if(StringUtils.isNotBlank(user.getName())){
				criteria.andLike("name","%"+user.getName()+"%");
				//criteria.andNameLike("%"+user.getName()+"%");
			}
			if(StringUtils.isNotBlank(user.getStatus())){
				criteria.andLike("status","%"+user.getStatus()+"%");
				//criteria.andStatusLike("%"+user.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(user.getHeadPic())){
				criteria.andLike("headPic","%"+user.getHeadPic()+"%");
				//criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}
			if(StringUtils.isNotBlank(user.getQq())){
				criteria.andLike("qq","%"+user.getQq()+"%");
				//criteria.andQqLike("%"+user.getQq()+"%");
			}
			if(StringUtils.isNotBlank(user.getIsMobileCheck())){
				criteria.andLike("isMobileCheck","%"+user.getIsMobileCheck()+"%");
				//criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}
			if(StringUtils.isNotBlank(user.getIsEmailCheck())){
				criteria.andLike("isEmailCheck","%"+user.getIsEmailCheck()+"%");
				//criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}
			if(StringUtils.isNotBlank(user.getSex())){
				criteria.andLike("sex","%"+user.getSex()+"%");
				//criteria.andSexLike("%"+user.getSex()+"%");
			}
	
		}
        List<TbUser> all = userMapper.selectByExample(example);
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	@Override
	public void getCode(String phone) {
		//生产者提供手机号,短信签名,短信模板,验证码给消费者

		String smsCode = (long)((Math.random()*9+1)*100000)+"";

		redisTemplate.boundHashOps("smsCode").put("register"+phone,smsCode);
		//设置验证码有效时间
		redisTemplate.boundHashOps("smsCode").expire(48L, TimeUnit.DAYS);

		Map<String,String> map= new HashMap<>();

		map.put("mobile",phone);
		map.put("sign_name",Sign_Name);
		map.put("template_code",TemplateCode);
		//"{\"code\":\"888888\"}"
		map.put("param","{\"code\":\""+smsCode+"\"}");

		try {
			Message msg = new Message("SMS_TOPIC","SMS_TAG",JSON.toJSONString(map).getBytes());
			SendResult send = producer.send(msg);
			System.out.println("====>"+send);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkCode(String smsCode,String phone) {
		String smsCode1 = (String) redisTemplate.boundHashOps("smsCode").get("register" + phone);
		if(smsCode1==null){
			return false;
		}

		if(!smsCode.equals(smsCode1)){
			return false;
		}

		return true;
	}

}
