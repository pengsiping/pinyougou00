package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.dao.ItemSearchDao;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemSearchDao itemSearchDao;


    @Override
    public Map<String, Object> search(Map<String, Object> search) {
        Map<String,Object> map = new HashMap<>();

        //获取查询条件
        String keywords = (String) search.get("keyword");

        //创建查询对象的构建对象
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        //创建查询条件
        //searchQueryBuilder.withQuery(QueryBuilders.matchQuery("keyword",keywords));
        //根据关键字搜索
        if(StringUtils.isNotBlank(keywords)){

            searchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords, "seller", "category", "brand", "title"));

            searchQueryBuilder.addAggregation(AggregationBuilders.terms("category_group").field("category").size(50));
        }else{
            searchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //按分类搜索
        String category = (String) search.get("category");
        /*if(StringUtils.isNotBlank(category)){
            searchQueryBuilder.withFilter(QueryBuilders.termsQuery("category",category));
        }*/

        if (StringUtils.isNotBlank(category)){
            boolQueryBuilder.filter(QueryBuilders.termQuery("category",category));
        }

        //按品牌搜索
        String brand= (String) search.get("brand");
        if(StringUtils.isNotBlank(brand)){
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand",brand));
        }

        //按规格搜索
       // Map<String,String> map1 = (Map) search.get("spec");//{网络: "联通4G"}

        /*if(map1!=null){
            for (String s : map1.keySet()) {
                //keyword为pojo里面的copyTO="keyword   specMap.网络.keyword
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap."+s+".keyword",map1.get(s)));
            }
        }*/
        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.网络.keyword","联通4G"));

        // 按价格查询
        String price = (String) search.get("price");
        if(StringUtils.isNotBlank(price)){
            String[] split = price.split("-");
            if("*".equals(split[1])){
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            } else {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0]).to(split[1]));
            }
        }

        //分页查询
        Integer pageNo = (Integer) search.get("pageNo");
        Integer pageSize = (Integer) search.get("pageSize");
        if(pageNo==null&&pageSize==null){
            pageNo=1;
            pageSize=40;
        }

        searchQueryBuilder.withPageable(PageRequest.of(pageNo, pageSize));

        searchQueryBuilder.withFilter(boolQueryBuilder);

        //builder.withQuery(QueryBuilders.multiMatchQuery(keywords,"seller","category","brand","title"));
        //设置高亮
        searchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("title"))
                .withHighlightBuilder(new HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));



        //构建查询对象
        NativeSearchQuery build = searchQueryBuilder.build();

        //价格排序

        String sortField = (String) search.get("sortField");
        String sortType = (String) search.get("sortType");

        if(StringUtils.isNotBlank(sortField)&&StringUtils.isNotBlank(sortType)){
            if("ASC".equals(sortType)){
                Sort sort = new Sort(Sort.Direction.ASC,sortField);
                build.addSort(sort);
            }else if("DESC".equals(sortType)){
                Sort sort = new Sort(Sort.Direction.DESC,sortField);
                build.addSort(sort);
            } else{
                System.out.println("不排序");
            }
        }

        //执行查询对象
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(build, TbItem.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                //获取命中目标
                SearchHits hits = searchResponse.getHits();
                List<T> content = new ArrayList<>();
                //如果没有命中目标
                if(hits==null||hits.getHits().length<0){
                    return new AggregatedPageImpl<T>(content);
                }
                for (SearchHit hit : hits) {
                    String sourceAsString = hit.getSourceAsString();
                    TbItem tbItem = JSON.parseObject(sourceAsString,TbItem.class);

                    //获取高亮
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();

                    //获取高亮的域的title的高亮对象
                    HighlightField highlightField = highlightFields.get("title");

                    if(highlightField!=null){
                        //获取高亮碎片
                        Text[] fragments = highlightField.getFragments();
                        StringBuffer sb = new StringBuffer();
                        if(fragments!=null){
                            for (Text fragment : fragments) {
                                sb.append(fragment.toString());  //获取高亮碎片的值
                            }
                        }
                        if(sb!=null){
                            tbItem.setTitle(sb.toString());
                        }
                    }
                    content.add((T) tbItem);
                }
                //???
                AggregatedPageImpl aggregatedPage = new AggregatedPageImpl(content,pageable,hits.getTotalHits(),searchResponse.getAggregations(),searchResponse.getScrollId());

                return aggregatedPage;

            }
        });

        //获取分组结果
        Aggregation category_group = tbItems.getAggregation("category_group");
        StringTerms category_group1 = (StringTerms) category_group;
        //商品分组结果
        List<String> categoryList = new ArrayList<>();

        if(category_group1!=null){
            for (StringTerms.Bucket bucket : category_group1.getBuckets()) {
                categoryList.add(bucket.getKeyAsString());
            }
        }



      /*  //添加品牌规格
        if (categoryList!=null&&categoryList.size()>0){
            Map brandAndSpec = findBrandAndSpec(categoryList.get(0));

            map.putAll(brandAndSpec);
        }*/

        //判断类别是否为空
        if(StringUtils.isNotBlank(category)){
            Map brandAndSpec = findBrandAndSpec(category);
            map.putAll(brandAndSpec);
        } else{
            if (categoryList!=null&&categoryList.size()>0) {
                Map brandAndSpec = findBrandAndSpec(categoryList.get(0));
                map.putAll(brandAndSpec);
            }
        }


        List<TbItem> content = tbItems.getContent();  //获取结果集

        long totalElements = tbItems.getTotalElements();  //获取总条数

        int totalPages = tbItems.getTotalPages();  //获取总页数



        map.put("rows",content);  //封装结果

        map.put("total",totalElements);  //封装总条数

        map.put("totalPages",totalPages); //封装总页数

        map.put("categoryList",categoryList);  //封装分组结果



        return map;

    }



    public Map findBrandAndSpec(String category){
        Map map= new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if(typeId!=null){
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            List brandList= (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList",brandList);
            map.put("specList",specList);
        }

        return map;
    }

    @Override
    public void updateIndex(List<TbItem> tbItemListByIds) {
        if(tbItemListByIds!=null&&tbItemListByIds.size()>0){
            for (TbItem tbItem : tbItemListByIds) {
                String spec = tbItem.getSpec();
                Map maps = JSON.parseObject(spec, Map.class);
                tbItem.setSpecMap(maps);
                //itemSearchDao.save(tbItem);
            }
           itemSearchDao.saveAll(tbItemListByIds);

        }

    }

    @Override
    public void deleteByIds(Long[] ids) {
        /*for (Long id : ids) {
            TbItem tbItem = new TbItem();
            tbItem.setGoodsId(id);
            itemSearchDao.delete(tbItem);
        }*/

        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(QueryBuilders.termsQuery("goodsId",ids));
        elasticsearchTemplate.delete(deleteQuery,TbItem.class);
    }



}
