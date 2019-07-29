package com.pinyougou.mapper;

import com.pinyougou.pojo.TbItemCat;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TbItemCatMapper extends Mapper<TbItemCat> {

    // 根据父类id - 0 , 查询分类信息
    List<TbItemCat> selectByParentId(@Param("id") Long id);


    //查询楼层标题信息
    List<TbItemCat> selectForFloorTitle(Long parentId);
}