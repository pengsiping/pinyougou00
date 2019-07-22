package com.pinyougou;

import com.pinyougou.es.dao.ItemDao;
import com.pinyougou.model.TbItem;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring-es.xml")
public class ElasticSearchTest {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testCreateIndexAndMapping(){
        //创建索引
        elasticsearchTemplate.createIndex(TbItem.class);
        //创建映射
        elasticsearchTemplate.putMapping(TbItem.class);
    }


    @Test
    public void saveData(){
        TbItem tbItem = new TbItem();
        tbItem.setId(10L);
        tbItem.setBrand("三星");
        tbItem.setTitle("测试");



        itemDao.save(tbItem);
    }

    @Test
    public void deleteData(){
        itemDao.deleteById(10L);
    }

    @Test
    public void update(){
        //id相同,使用save方法自动更新
        TbItem tbitem = new TbItem();
        tbitem.setId(10L);
        tbitem.setTitle("测试商品111");
        tbitem.setCategory("商品分类111");
        tbitem.setBrand("三星");
        tbitem.setSeller("三星旗舰店");
        Map<String,String> map = new HashMap<>();
        map.put("网络制式","移动4G");
        map.put("机身内存","16G");
        tbitem.setMap(map);

        itemDao.save(tbitem);
    }

    //查询所有
    @Test
    public void queryAll(){
        Iterable<TbItem> list = itemDao.findAll();
        for (TbItem tbItem : list) {

            System.out.println(tbItem.getTitle());
        }
    }

    //根据ID查询
    @Test
    public void findById(){
        Optional<TbItem> tbItem = itemDao.findById(10L);
        System.out.println(tbItem.get().getTitle());
    }

    //分页查询
    @Test
    public void queryPageable(){
        Pageable pageable = PageRequest.of(0,10);
        Page<TbItem> tbItems = itemDao.findAll(pageable);
        for (TbItem tbItem : tbItems) {

            System.out.println(tbItem);
            System.out.println(tbItem.getTitle());
        }
    }

    //通配符查询
    @Test
    public void queryByWildQuery(){

        SearchQuery query = new NativeSearchQuery(QueryBuilders.wildcardQuery("title","商?"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);

        long totalElements = tbItems.getTotalElements();
        System.out.println("总记录数"+totalElements);

        List<TbItem> content = tbItems.getContent();
        for (TbItem tbItem : content) {
            System.out.println(tbItem);
        }
    }

    //分词查询
    @Test
    public void queryMatchQuery(){
        SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.matchQuery("title","商品"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem);
        }
    }


    @Test
    public void queryByCopyTo(){
        SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.matchQuery("keyword","测试"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem);
        }
    }

    @Test
    public void queryByObject(){
        SearchQuery searchQuery = new NativeSearchQuery(QueryBuilders.matchQuery("map.网络制式.keyword","移动4G"));
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem);
        }
    }

    @Test
    public void queryByFilter(){
        //1.创建查询对象的构建对象
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        //2.创建 查询条件
        searchQueryBuilder.withIndices("pinyougou");
        searchQueryBuilder.withTypes("item");
        searchQueryBuilder.withQuery(QueryBuilders.matchQuery("title", "商品"));

        //3.创建 过滤查询(规格的过滤查询 多个过滤使用bool查询)

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery("map.网络制式.keyword","移动4G"));
        boolQueryBuilder.filter(QueryBuilders.termQuery("map.机身内存.keyword","16G"));

        searchQueryBuilder.withFilter(boolQueryBuilder);

        NativeSearchQuery build = searchQueryBuilder.build();
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(build, TbItem.class);
        /*for (TbItem tbItem : tbItems) {
            System.out.println(tbItem);
        }*/

        List<TbItem> content = tbItems.getContent();
        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle()+"-==="+tbItem.getMap());
        }
    }

}
