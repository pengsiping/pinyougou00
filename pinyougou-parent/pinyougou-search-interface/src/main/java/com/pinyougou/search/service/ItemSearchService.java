package com.pinyougou.search.service;




import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    Map<String,Object> search (Map<String,Object> search);


    public void updateIndex(List<TbItem> list);

    public void deleteByIds(Long[] ids);


}
