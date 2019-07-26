package com.pinyougou.mapper;

import com.pinyougou.pojo.TbItemCat;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TbItemCatMapper extends Mapper<TbItemCat> {


    List<TbItemCat> selectItemCatList(@Param("parentId") Long parentId);

}