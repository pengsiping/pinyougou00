package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private FreeMarkerConfigurer markerConfigurer;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Value("${pageDir}")
    private String pageDir;



    @Override
    public void genItemHtml(Long goodsId) throws Exception {

        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);

        genHtml("item.ftl",tbGoods,tbGoodsDesc);


    }
    //删除静态页面
    @Override
    public void deleteItemHtml(Long[] ids) {
        for (Long id : ids) {
            try {
                FileUtils.forceDelete(new File( pageDir+id+".html"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void genHtml(String templateName, TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) throws Exception {
        FileWriter writer = null;

        try {

            Long category1Id = tbGoods.getCategory1Id();
            Long category2Id = tbGoods.getCategory2Id();
            Long category3Id = tbGoods.getCategory3Id();

            TbItemCat cat1 = tbItemCatMapper.selectByPrimaryKey(category1Id);
            TbItemCat cat2 = tbItemCatMapper.selectByPrimaryKey(category2Id);
            TbItemCat cat3 = tbItemCatMapper.selectByPrimaryKey(category3Id);


            //获取SKU所有规格
            Example example = new Example(TbItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("goodsId",tbGoods.getId());
            criteria.andEqualTo("status","1");
            example.setOrderByClause("is_default Desc");  //降序排列
            List<TbItem> tbItems = tbItemMapper.selectByExample(example);



            //1.创建一个configuration对象
            //2.设置字符编码 和 模板加载的目录---配置文件
            Configuration configuration = markerConfigurer.getConfiguration();


            //创建模板
            Template template1= configuration.getTemplate(templateName);

            Map model = new HashMap();

            model.put("tbGoods",tbGoods);
            model.put("tbGoodsDesc",tbGoodsDesc);
            model.put("cat1",cat1);
            model.put("cat2",cat2);
            model.put("cat3",cat3);
            model.put("skuList",tbItems);

            //输出
            writer = new FileWriter(new File(pageDir + tbGoods.getId() + ".html"));

            template1.process(model,writer);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
