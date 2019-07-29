package com.pinyougou.search.service;

import com.pinyougou.pojo.TbGoods;

import java.util.List;

public interface GoodsSearchService {

    void updateOnSaleGoods(List<TbGoods> tbGoodsList);

    void deleteSaleGoods(Long[] ids);
}
