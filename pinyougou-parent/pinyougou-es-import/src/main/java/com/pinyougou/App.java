package com.pinyougou;

import com.pinyougou.es.service.GoodsSearchService;
import com.pinyougou.es.service.ItemService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        ApplicationContext application = new ClassPathXmlApplicationContext("classpath:spring/applicationContext_*.xml");
        /*ItemService itemService = application.getBean(ItemService.class);
        itemService.importDataToEs();*/

        GoodsSearchService goodsSearchService = application.getBean(GoodsSearchService.class);
        goodsSearchService.updateOnSaleGoods();
    }
}
