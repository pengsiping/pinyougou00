package com.pinyougou;

import static org.junit.Assert.assertTrue;

import com.pinyougou.es.service.ItemService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void test(){
        ApplicationContext application = new ClassPathXmlApplicationContext("classpath:spring/applicationContext_es.xml");
        ItemService itemService = application.getBean(ItemService.class);
        itemService.importDataToEs();
    }
}
