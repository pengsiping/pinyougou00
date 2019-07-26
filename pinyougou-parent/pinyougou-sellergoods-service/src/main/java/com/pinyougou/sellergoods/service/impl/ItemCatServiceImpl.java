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


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ItemCatServiceImpl extends CoreServiceImpl<TbItemCat> implements ItemCatService {


    private TbItemCatMapper itemCatMapper;


    @Autowired
    public ItemCatServiceImpl(TbItemCatMapper itemCatMapper) {
        super(itemCatMapper, TbItemCat.class);
        this.itemCatMapper = itemCatMapper;
    }


    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbItemCat> all = itemCatMapper.selectAll();
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize, TbItemCat itemCat) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();

        if (itemCat != null) {
            if (StringUtils.isNotBlank(itemCat.getName())) {
                criteria.andLike("name", "%" + itemCat.getName() + "%");
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
    public List<TbItemCat> findByParentId(Long parentId) {
        TbItemCat cat = new TbItemCat();
        cat.setParentId(parentId);
        List<TbItemCat> tbItemCats = itemCatMapper.select(cat);

        //根据typeId获取相对应的手机品牌及规格
        for (TbItemCat tbItemCat : tbItemCats) {
            redisTemplate.boundHashOps("itemCat").put(tbItemCat.getName(), tbItemCat.getTypeId());
        }
        return tbItemCats;
    }


    /**
     * 获取商品分类
     *
     * @param parentId
     * @return
     */

    @Override
    public List<TbItemCat> findItemList(Long parentId) {
        List<TbItemCat> itemCatList01 = (List<TbItemCat>) redisTemplate.boundValueOps("itemCat03").get();
        if (itemCatList01 == null) {

            itemCatList01 = new ArrayList<>();

            //设置一级目录
            List<TbItemCat> tbItemCatList01 = itemCatMapper.selectItemCatList(parentId);
            for (TbItemCat tbItemCat : tbItemCatList01) {
                TbItemCat tbItemCat01 = new TbItemCat();
                tbItemCat01.setId(tbItemCat.getId());
                tbItemCat01.setName(tbItemCat.getName());
                tbItemCat01.setParentId(tbItemCat.getParentId());

                Example example = new Example(TbItemCat.class);
                example.createCriteria().andEqualTo("parentId",tbItemCat01.getId());
                List<TbItemCat> tbItemCatList02 = itemCatMapper.selectByExample(example);

                List<TbItemCat> itemCatList02 = new ArrayList<>();
                //设置二级目录
                for (TbItemCat itemCat : tbItemCatList02) {
                    TbItemCat tbItemCat02 = new TbItemCat();
                    tbItemCat02.setId(itemCat.getId());
                    tbItemCat02.setParentId(itemCat.getParentId());
                    tbItemCat02.setName(itemCat.getName());
                    //根据二级目录id查找三级目录
                    Example example1 = new Example(TbItemCat.class);
                    example.createCriteria().andEqualTo("parentId",tbItemCat01.getId());
                    List<TbItemCat> tbItemCatList03 = itemCatMapper.selectByExample(example1);
                    tbItemCat02.setList(tbItemCatList03);

                    List<TbItemCat> itemCatList03 = new ArrayList<>();

                    //设置三级目录
                    for (TbItemCat cat : tbItemCatList03) {
                        TbItemCat tbItemCat03 = new TbItemCat();
                        tbItemCat03.setName(cat.getName());
                        //设置三级目录列表
                        itemCatList03.add(tbItemCat03);
                    }
                    //设置二级目录里面的三级列表
                    tbItemCat02.setList(itemCatList03);
                    itemCatList02.add(tbItemCat02);
                }
                //设置一级目录里面的二级目录列表
                tbItemCat01.setList(itemCatList02);
                itemCatList01.add(tbItemCat01);
            }

        }
        redisTemplate.boundValueOps("itemCat03").set(itemCatList01);
        return itemCatList01;
    }

}
