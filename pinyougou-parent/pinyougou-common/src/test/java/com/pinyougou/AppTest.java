package com.pinyougou;

/**
 * Unit test for simple App.
 */
public class AppTest {
    class person {
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    /*@Test
    public void testPOI() {
        String[] strings = {"id"};
        String[] titles = {"id"};
        List<person> list = new ArrayList<>();
        person p1 = new person();
        p1.setId(1);
        list.add(p1);
        try {
            POIUtils.exportExcel("C:\\temp\\a.xls", "A", list, titles, strings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
