package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.search.dao.GoodsSearchDao;
import com.pinyougou.search.service.GoodsSearchService;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;

import java.util.List;

@Service
public class GoodsSearchServiceImpl implements GoodsSearchService {

    @Autowired
    private GoodsSearchDao goodsSearchDao;

    @Autowired
    private ElasticsearchTemplate template;

    @Override
    public void updateOnSaleGoods(List<TbGoods> tbGoodsList) {
       goodsSearchDao.saveAll(tbGoodsList);
    }

    @Override
    public void deleteSaleGoods(Long[] ids) {
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(QueryBuilders.termsQuery("id",ids));
        template.delete(deleteQuery,TbGoods.class);
    }
}
