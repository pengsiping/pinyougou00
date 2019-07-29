package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.sellergoods.service.GoodsService;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl extends CoreServiceImpl<TbGoods> implements GoodsService {


    private TbGoodsMapper goodsMapper;

    @Autowired
    public GoodsServiceImpl(TbGoodsMapper goodsMapper) {
        super(goodsMapper, TbGoods.class);
        this.goodsMapper = goodsMapper;
    }

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbSellerMapper tbSellerMapper;

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Autowired
    private TbItemMapper tbItemMapper;


    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbGoods> all = goodsMapper.selectAll();
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods goods) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete","0");

        if (goods != null) {
            if (StringUtils.isNotBlank(goods.getSellerId())) {
                //criteria.andLike("sellerId","%"+goods.getSellerId()+"%");
                //criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
                criteria.andEqualTo("sellerId", goods.getSellerId());
            }
            if (StringUtils.isNotBlank(goods.getGoodsName())) {
                criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
                //criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
            }
            if (StringUtils.isNotBlank(goods.getAuditStatus())) {
                //criteria.andLike("auditStatus","%"+goods.getAuditStatus()+"%");
                //criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
                criteria.andEqualTo("auditStatus", goods.getAuditStatus());
            }
            if (StringUtils.isNotBlank(goods.getIsMarketable())) {
                criteria.andLike("isMarketable", "%" + goods.getIsMarketable() + "%");
                //criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
            }
            if (StringUtils.isNotBlank(goods.getCaption())) {
                criteria.andLike("caption", "%" + goods.getCaption() + "%");
                //criteria.andCaptionLike("%"+goods.getCaption()+"%");
            }
            if (StringUtils.isNotBlank(goods.getSmallPic())) {
                criteria.andLike("smallPic", "%" + goods.getSmallPic() + "%");
                //criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
            }
            if (StringUtils.isNotBlank(goods.getIsEnableSpec())) {
                criteria.andLike("isEnableSpec", "%" + goods.getIsEnableSpec() + "%");
                //criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
            }

        }
        List<TbGoods> all = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Override
    public void add(Goods goods) {
        TbGoods tbGoods = goods.getTbGoods();
        tbGoods.setAuditStatus("0");
        tbGoods.setIsDelete(false);
        goodsMapper.insert(tbGoods);

        TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
        tbGoodsDesc.setGoodsId(tbGoods.getId());
        goodsDescMapper.insert(tbGoodsDesc);

        List<TbItem> list = goods.getTbItemList();
        for (TbItem tbItem : list) {
            String spec = tbItem.getSpec(); //{"网络":"移动4G","机身内存":"16G"}
            String title = tbGoods.getGoodsName();
            Map<String, String> map = JSON.parseObject(spec, Map.class);
            for (String key : map.keySet()) {
                String s = map.get(key);
                title += " " + s;
            }
            tbItem.setTitle(title);

            //设置image
            //[{"color":"hongse","url":"http://192.168.25.129/group1/M00/00/04/wKgZhVrkS-WAGXXyAAJQksgqu3o373.jpg"}]
            String itemImages = tbGoodsDesc.getItemImages();
            //todo 增加非空判断
            List<Map> itemImage = JSON.parseArray(itemImages, Map.class);
            String url = (String) itemImage.get(0).get("url");
            tbItem.setImage(url);

            //设置category及categoryId
            TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            tbItem.setCategory(tbItemCat.getName());
            tbItem.setCategoryid(tbItemCat.getId());

            //时间
            tbItem.setCreateTime(new Date());
            tbItem.setUpdateTime(new Date());

            tbItem.setGoodsId(tbGoods.getId());


            //设置商家
            TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
            tbItem.setSellerId(tbSeller.getSellerId());
            tbItem.setSeller(tbSeller.getNickName());//店铺名

            //设置品牌明后
            TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
            tbItem.setBrand(tbBrand.getName());

            tbItemMapper.insert(tbItem);
        }


    }

    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setTbGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setTbGoodsDesc(tbGoodsDesc);

        TbItem tbItem = new TbItem();
        tbItem.setGoodsId(id);
        List<TbItem> select = tbItemMapper.select(tbItem);
        goods.setTbItemList(select);
        return goods;
    }

    @Override
    public Goods update(Goods goods) {
        TbGoods tbGoods = goods.getTbGoods();
        tbGoods.setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(tbGoods);
        TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
        goodsDescMapper.updateByPrimaryKey(tbGoodsDesc);

        //更新SKU
        TbItem tbItem = new TbItem();
        tbItem.setGoodsId(tbGoods.getId());
        tbItemMapper.delete(tbItem);
        saveItems(goods, tbGoods, tbGoodsDesc);
        return goods;
    }

    @Override
    public void updateStatus(String status,Long[] ids) {
        /*for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }*/
        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(ids));
        TbGoods tbGoods= new TbGoods();
        tbGoods.setAuditStatus(status);
        goodsMapper.updateByExampleSelective(tbGoods,example);
    }

    @Override
    public List<TbItem> findTbItemListByIds(Long[] ids) {
        Example example = new Example(TbItem.class);
        example.createCriteria().andIn("goodsId",Arrays.asList(ids)).andEqualTo("status","1");

        return tbItemMapper.selectByExample(example);
    }

    private void saveItems(Goods goods, TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {
        if ("1".equals(tbGoods.getIsEnableSpec())) {
            List<TbItem> tbItemList = goods.getTbItemList();
            for (TbItem tbItem : tbItemList) {
                String spec = tbItem.getSpec(); //{"网络":"移动4G","机身内存":"16G"}
                String title = tbGoods.getGoodsName();
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                for (String key : map.keySet()) {
                    String s = map.get(key);
                    title += " " + s;
                }
                tbItem.setTitle(title);

                //设置image
                //[{"color":"hongse","url":"http://192.168.25.129/group1/M00/00/04/wKgZhVrkS-WAGXXyAAJQksgqu3o373.jpg"}]
                String itemImages = tbGoodsDesc.getItemImages();

                List<Map> itemImage = JSON.parseArray(itemImages, Map.class);
                String url = (String) itemImage.get(0).get("url");
                tbItem.setImage(url);

                //设置category及categoryId
                TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
                tbItem.setCategory(tbItemCat.getName());
                tbItem.setCategoryid(tbItemCat.getId());

                //时间
                tbItem.setCreateTime(new Date());
                tbItem.setUpdateTime(new Date());

                tbItem.setGoodsId(tbGoods.getId());


                //设置商家
                TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
                tbItem.setSellerId(tbSeller.getSellerId());
                tbItem.setSeller(tbSeller.getNickName());//店铺名

                //设置品牌明后
                TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
                tbItem.setBrand(tbBrand.getName());

                tbItemMapper.insert(tbItem);
            }
        } else {
            TbItem tbItem = new TbItem();
            tbItem.setTitle(tbGoods.getGoodsName());
            tbItem.setPrice(tbGoods.getPrice());
            tbItem.setNum(999);
            String itemImages = tbGoodsDesc.getItemImages();
            List<Map> maps = JSON.parseArray(itemImages, Map.class);
            tbItem.setImage(maps.get(0).get("url").toString());

            //设置category及categoryId
            TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            tbItem.setCategory(tbItemCat.getName());
            tbItem.setCategoryid(tbItemCat.getId());

            //时间
            tbItem.setCreateTime(new Date());
            tbItem.setUpdateTime(new Date());

            tbItem.setGoodsId(tbGoods.getId());


            //设置商家
            TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
            tbItem.setSellerId(tbSeller.getSellerId());
            tbItem.setSeller(tbSeller.getNickName());//店铺名

            //设置品牌明后
            TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
            tbItem.setBrand(tbBrand.getName());

            tbItemMapper.insert(tbItem);
        }
    }


    @Override
    public void delete(Object[] ids) {

        for (Object id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete(true);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }

    }
}
