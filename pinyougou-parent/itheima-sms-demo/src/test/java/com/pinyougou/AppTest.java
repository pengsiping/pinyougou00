package com.pinyougou;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void test() {
        String a = "22";
        String b = "22";

        Integer c=128;
        Integer d=128;  //(-128~127)
        System.out.println(a == b);
        System.out.println(c==d);


    }

}
