package com.pinyougou;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for simple App.
 */

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:spring-consumer.xml")
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void consumer() throws InterruptedException {
        Thread.sleep(10000);
    }
}
