package com.pinyougou.user.service.impl;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.pinyougou.SysConstants;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.UserService;
import entity.Cart;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;




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

	@Autowired
	private TbAnalysePVMapper analysePVMapper;

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
	@Autowired
	private TbOrderMapper tbOrderMapper;
	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;


	@Override
	public Map<String, Object> showChart() {
		Map<String, Object> map = new HashMap<>();
		Example example = new Example(TbAnalysePV.class);
		Example.Criteria criteria = example.createCriteria();
		Calendar c1 = Calendar.getInstance();
		c1.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH)-1);
		criteria.andGreaterThanOrEqualTo("endTime",c1.getTime());
		List<TbAnalysePV> list = analysePVMapper.selectByExample(example);
		List<Long> count = new ArrayList<>();
		List<String> time = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		for (TbAnalysePV tbAnalysePV : list) {
			count.add(tbAnalysePV.getNum());
			time.add(format.format(tbAnalysePV.getStartTime()));
		}
		map.put("time", time);
		map.put("count", count);
		return map;
	}

	@Override
	public void delete(Object[] ids) {
		Example example = new Example(TbUser.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", Arrays.asList(ids));
		TbUser user= new TbUser();
		user.setStatus("N");
		userMapper.updateByExampleSelective(user, example);
	}
	@Override
	public List<TbOrder> findUnpayOrders(String userId) {
		List<TbOrder> tbOrders=new ArrayList<>();
		if(userId!=null){
			//TbOrder tbOrder=new TbOrder();
			//根据条件查出对应的未付款订单
			Example example=new Example(TbOrder.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("userId",userId);
			criteria.andEqualTo("status","1");
			tbOrders= tbOrderMapper.selectByExample(example);
			if(tbOrders!=null&&tbOrders.size()>0){
				for (TbOrder order : tbOrders) {
					//TbOrderItem tbOrderItem=new TbOrderItem();
					//tbOrderItem.setOrderId(order.getOrderId());
					Example example1=new Example(TbOrderItem.class);
					Example.Criteria criteria1 = example1.createCriteria();
					criteria1.andEqualTo("orderId",order.getOrderId());
					List<TbOrderItem> orderItems = tbOrderItemMapper.selectByExample(example1);
					if(orderItems!=null&&orderItems.size()>0){
						//通过temtId获取spec
						for (TbOrderItem orderItem : orderItems) {
							TbItem tbItem = tbItemMapper.selectByPrimaryKey(orderItem.getItemId());
							orderItem.setSpec(tbItem.getSpec());
						}
						//设置tbOrder的items属性
						order.setTbOrderItems(orderItems);
					}

				}
			}
		} return tbOrders;
	}



    @Override
    public List<TbOrder> findMyOrders(String userId) {
		List<TbOrder> tbOrders=new ArrayList<>();
        if(userId!=null){
            //TbOrder tbOrder=new TbOrder();
            //根据条件查出对应的未付款订单
            Example example=new Example(TbOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId",userId);
            //criteria.andEqualTo("status","1");
            tbOrders= tbOrderMapper.selectByExample(example);
            if(tbOrders!=null&&tbOrders.size()>0){
                for (TbOrder order : tbOrders) {
					order.setOrderIdStr(order.getOrderId().toString());
                    //TbOrderItem tbOrderItem=new TbOrderItem();
                    //tbOrderItem.setOrderId(order.getOrderId());
                    Example example1=new Example(TbOrderItem.class);
                    Example.Criteria criteria1 = example1.createCriteria();
                    criteria1.andEqualTo("orderId",order.getOrderId());
                    List<TbOrderItem> orderItems = tbOrderItemMapper.selectByExample(example1);
                    if(orderItems!=null&&orderItems.size()>0){
                        //通过temtId获取spec
                        for (TbOrderItem orderItem : orderItems) {
                            TbItem tbItem = tbItemMapper.selectByPrimaryKey(orderItem.getItemId());
                            orderItem.setSpec(tbItem.getSpec());
                        }
						//设置tbOrder的items属性
						order.setTbOrderItems(orderItems);
                    }

                }
            }
        } return tbOrders;

    }
	@Autowired
	private TbUserMapper tbUserMapper;


	@Autowired
	private TbAddressMapper tbAddressMapper;


	@Override
	public List<TbAddress> findAddress(String userId) {
		Example example=new Example(TbAddress.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId",userId);
		//根据用户名查询地址
		List<TbAddress> addresses=  tbAddressMapper.selectByExample(example);
		return addresses;
	}

	@Override
	public int deleteAddress(Integer index,String userId) {
		List<TbAddress> addresses = findAddress(userId);
		int i=0;
		if(addresses!=null && addresses.size()>0){
			for (int j = 0; j < addresses.size(); j++) {
				TbAddress address = addresses.get(index);
				i = tbAddressMapper.delete(address);
			}
		}
		return i;
	}

	@Override
	public int addAddress(String userId,TbAddress item) {
		TbAddress address=new TbAddress();
		address.setUserId(userId);//设置用户id
		address.setProvinceId(item.getProvinceId());//设置省
		address.setCityId(item.getCityId());//设置市
		address.setTownId(item.getTownId());//设置县
		address.setMobile(item.getMobile());//设置手机号
		address.setAddress(item.getAddress());//设置详细地址
		address.setContact(item.getContact());//设置联系人
		address.setIsDefault("0");//设置不默认
		address.setCreateDate(new Date());//设置创建日期
		address.setAlias(item.getAlias());//设置别名
		int i = tbAddressMapper.insert(address);
		return i;
	}
	//设置地址默认值
	@Override
	public void setDefaultAddress(Integer index,String userId) {
		List<TbAddress> addresses = findAddress(userId);
		if(addresses!=null && addresses.size()>0){
			for (int i = 0; i < addresses.size(); i++) {
				if(i==index){
					TbAddress address = addresses.get(i);
					address.setIsDefault("1");
					tbAddressMapper.updateByPrimaryKey(address);
				}else {
					TbAddress address = addresses.get(i);
					address.setIsDefault("0");
					tbAddressMapper.updateByPrimaryKey(address);
				}
			}
		}
	}

    @Override
    public TbAddress findOneAddress(Long id) {
        TbAddress address = tbAddressMapper.selectByPrimaryKey(id);
        return address;
    }

	@Override
	public int updateAddress(TbAddress item) {
		int i = tbAddressMapper.updateByPrimaryKey(item);
		return i;
	}

	@Override
	public void register(TbUser userInfo,String userName) {
        TbUser tbUser=new TbUser();
        tbUser.setUsername(userName);
		TbUser user = tbUserMapper.selectOne(tbUser);

		user.setNickName(userInfo.getNickName());//设置昵称
        user.setProvinceId(userInfo.getProvinceId());//设置省
        user.setCityId(userInfo.getCityId());//设置城市
        user.setTownId(userInfo.getTownId());//设置区
        user.setJob(userInfo.getJob());//设置职业
        user.setBirthday(userInfo.getBirthday());//设置生日
        user.setSex(userInfo.getSex());//设置性别
        user.setHeadPic(userInfo.getHeadPic());//设置头像
		tbUserMapper.updateByPrimaryKey(user);
    }
	/**
	 * 商品详情页面加载生成传递goodsId
	 * 记录所有用户浏览过的商品详细信息到我的足迹中
	 * 足迹所要获取到的数据与我的收藏相似
	 *
	 * @return goodsId  tb_Goods表的id
	 */
	@Autowired
	private TbGoodsMapper tbGoodsMapper;
	@Override
	public void findMyFootprint(Long goodsId, String userId) {
		//定义一个list容器，假设有多个浏览商品使用list容器装
		List<Map<String, Object>> list =null;

		//第一次redis中没有浏览商品缓存时，创建list容器接收数据
		list  = (List<Map<String, Object>>) redisTemplate.boundHashOps
				(SysConstants.MY_FOOTPRINT + userId).get("FootList");

		//如果redis中为空
		if (list == null) {
			//创建list容器
			list = new ArrayList<>();
		}

		//商品详情页数据封装的map容器
		Map<String, Object> map = new HashMap<>();

		//1.根据goodsId获取itemId（商品id）
		TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);


		//获取默认的商品id
		Long itemId = tbGoods.getDefaultItemId();

		//2.根据商品id获取商品对象
		TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);

		//1.获取商品标题
		String title = tbItem.getTitle();

		//2.获取商品规格
		String spec = tbItem.getSpec();

		//3.获取商品价钱
		BigDecimal price = tbItem.getPrice();

		//4.获取商品库存数量
		Integer num = tbItem.getNum();

		//5.获取商品图片
		String image = tbItem.getImage();

		//将数据都封装到map中
		map.put("title", title);
		map.put("spec", spec);
		map.put("price", price);
		map.put("num", num);
		map.put("image", image);
		map.put("itemId", itemId);

		list.add(map);

		//将用户浏览的商品详细信息从左到右的形式压入队列中，使用户的名称做标记，此用户就是该队列
		redisTemplate.boundHashOps(SysConstants.MY_FOOTPRINT + userId).put("FootList", list);
	}
	/**
	 * 从redis中获取用户浏览的商品详细信息
	 * 商品详细信息从左到右压入队列，取从右取，当商品为空时，则从redis中删除此商品
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public List<Map<String, Object>> findAllFootprint(String userId) {
		//根据大key与用户名称标识从redis中取出list<Map>
		List<Map<String, Object>> footList = (List<Map<String, Object>>)
				redisTemplate.boundHashOps(SysConstants.MY_FOOTPRINT + userId).get("FootList");

		//判断当商品被加入购物车时，从redis中删除
		return footList;
	}

    private String[] source_type = {"PC","H5","Android","IOS","WeChat"};

    @Override
    public List<Map<String, String>> count(String type) {
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
			TbUser tbUser = new TbUser();
			tbUser.setSourceType(i+"");
			int count = userMapper.selectCount(tbUser);
			Map<String, String> map = new HashMap<>();
			map.put("name",source_type[i]);
			map.put("value", count+"");
			list.add(map);
		}
        return list;
    }
}
