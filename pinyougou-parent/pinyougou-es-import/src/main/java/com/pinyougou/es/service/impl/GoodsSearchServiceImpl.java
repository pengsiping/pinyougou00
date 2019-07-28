package com.pinyougou.es.service.impl;


import com.pinyougou.es.dao.GoodsSearchDao;
import com.pinyougou.es.service.GoodsSearchService;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.pojo.TbGoods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsSearchServiceImpl implements GoodsSearchService {

    @Autowired
    private GoodsSearchDao goodsSearchDao;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Override
    public void updateOnSaleGoods() {
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsMarketable("1");
        List<TbGoods> select = tbGoodsMapper.select(tbGoods);
        goodsSearchDao.saveAll(select);

    }
}
