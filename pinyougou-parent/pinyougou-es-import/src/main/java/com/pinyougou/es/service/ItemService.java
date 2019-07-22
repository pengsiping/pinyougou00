package com.pinyougou.es.service;

import com.pinyougou.pojo.TbItem;


public interface ItemService  {

    /*
    *
    * 从数据库获取数据导入ES索引库
    * */

    public void importDataToEs();
}
