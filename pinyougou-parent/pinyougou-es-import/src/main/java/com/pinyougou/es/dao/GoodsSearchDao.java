package com.pinyougou.es.dao;

import com.pinyougou.pojo.TbGoods;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface GoodsSearchDao extends ElasticsearchCrudRepository<TbGoods,Long> {
}
