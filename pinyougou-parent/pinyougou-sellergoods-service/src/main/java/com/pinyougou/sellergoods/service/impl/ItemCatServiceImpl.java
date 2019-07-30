package com.pinyougou.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbTypeTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;  

import com.pinyougou.sellergoods.service.ItemCatService;

import javax.annotation.Resource;


/**
 * 服务实现层
 *
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl extends CoreServiceImpl<TbItemCat> implements ItemCatService {

	
	private TbItemCatMapper itemCatMapper;


	@Autowired
	public ItemCatServiceImpl(TbItemCatMapper itemCatMapper) {
		super(itemCatMapper, TbItemCat.class);
		this.itemCatMapper=itemCatMapper;
	}



	@Autowired
    private RedisTemplate redisTemplate;

	
	

	
	@Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbItemCat> all = itemCatMapper.selectAll();
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize, TbItemCat itemCat) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();

        if(itemCat!=null){			
						if(StringUtils.isNotBlank(itemCat.getName())){
				criteria.andLike("name","%"+itemCat.getName()+"%");
				//criteria.andNameLike("%"+itemCat.getName()+"%");
			}
	
		}
        List<TbItemCat> all = itemCatMapper.selectByExample(example);
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Override
    public List<TbItemCat> findByParentId(Long parentId){
        // 1.先从redis缓存中 , 获取三级分类信息!
        List<TbItemCat> itemCat01List  = (List<TbItemCat>) redisTemplate.boundValueOps("itemCat03").get();
        //List<TbItemCat> itemCat01List=new ArrayList<>();
        // 2.若缓存中没有数据 , 从数据库中查询( 并放到缓存中 )
        if (itemCat01List==null){
            // 缓存穿透 -> 请求排队等候.
            synchronized (this){
                // 进行二次校验?
                itemCat01List  = (List<TbItemCat>) redisTemplate.boundValueOps("itemCat03").get();
                if (itemCat01List==null){
                    // 创建一个集合 , 存放一级分类
                    itemCat01List = new ArrayList<>();

                    // 根据parent_id = 0 , 获取一级分类信息!
                    List<TbItemCat> itemCatList = itemCatMapper.selectByParentId(parentId);
                    for (TbItemCat itemCat : itemCatList) {
                        // 设置一级分类信息!
                        TbItemCat itemCat01 = new TbItemCat();
                        itemCat01.setId(itemCat.getId());
                        itemCat01.setName(itemCat.getName());
                        itemCat01.setParentId(itemCat.getParentId());

                        // 根据一级分类的id -> 找到对应的二级分类!
                        List<TbItemCat> itemCatList02 = new ArrayList<>();
                        Example itemCatQuery02 = new Example(TbItemCat.class);
                        itemCatQuery02.createCriteria().andEqualTo("parentId",itemCat.getId());
                        List<TbItemCat> itemCat02List = itemCatMapper.selectByExample(itemCatQuery02);
                        for (TbItemCat itemCat2 : itemCat02List) {
                            // 设置二级分类信息!
                            TbItemCat itemCat02 = new TbItemCat();
                            itemCat02.setId(itemCat2.getId());
                            itemCat02.setName(itemCat2.getName());
                            itemCat02.setParentId(itemCat2.getParentId());


                            // 根据二级分类的id -> 找到对应的三级分类!
                            List<TbItemCat> itemCatList03 = new ArrayList<>();
                            Example itemCatQuery03 = new Example(TbItemCat.class);
                            itemCatQuery03.createCriteria().andEqualTo("parentId",itemCat02.getId());
                            List<TbItemCat> itemCat03List = itemCatMapper.selectByExample(itemCatQuery03);
                            for (TbItemCat itemCat3 : itemCat03List) {
                                itemCatList03.add(itemCat3);
                            }
                            itemCat02.setItemCatList(itemCatList03);  // 二级分类中 添加 三级分类.
                            itemCatList02.add(itemCat02);       // 添加二级分类.
                        }
                        itemCat01.setItemCatList(itemCatList02); // 一级分类中 添加 二级分类!
                        itemCat01List.add(itemCat01);  // 添加一级分类
                    }
                    // 将查询到的数据放入缓存中!
                    redisTemplate.boundValueOps("itemCat03").set(itemCat01List);
                    return itemCat01List;
               }
            }

        }

        // 3.若缓存中有数据 , 直接返回即可!
        return itemCat01List;

    }

    @Override
    public List<TbItemCat> findFloorTitle(Long parentId) {
        return itemCatMapper.selectForFloorTitle(parentId);
    }

}
