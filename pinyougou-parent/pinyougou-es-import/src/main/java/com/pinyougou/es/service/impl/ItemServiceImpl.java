package com.pinyougou.es.service.impl;




import com.alibaba.fastjson.JSON;
import com.pinyougou.es.dao.ItemDao;
import com.pinyougou.es.service.ItemService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private ItemDao itemDao;

    @Override
    public void importDataToEs() {
        TbItem tbItem = new TbItem();
        tbItem.setStatus("1");
        List<TbItem> list = tbItemMapper.select(tbItem);

        for (TbItem item : list) {
            String spec = item.getSpec();
            if(StringUtils.isNotBlank(spec)){
                Map map = JSON.parseObject(spec, Map.class);
                item.setSpecMap(map);
            }
        }
        itemDao.saveAll(list);
    }
}
