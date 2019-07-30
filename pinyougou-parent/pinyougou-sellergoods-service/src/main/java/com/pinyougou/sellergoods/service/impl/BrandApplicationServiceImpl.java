package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbBrandApplicationMapper;
import com.pinyougou.pojo.TbBrandApplication;
import com.pinyougou.sellergoods.service.BrandApplicationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.security.Security;
import java.util.Arrays;
import java.util.List;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class BrandApplicationServiceImpl extends CoreServiceImpl<TbBrandApplication>  implements BrandApplicationService {


	private TbBrandApplicationMapper brandApplicationMapper;

	@Autowired
	public BrandApplicationServiceImpl(TbBrandApplicationMapper brandApplicationMapper) {
		super(brandApplicationMapper, TbBrandApplication.class);
		this.brandApplicationMapper=brandApplicationMapper;
	}

	
	

	
	@Override
    public PageInfo<TbBrandApplication> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbBrandApplication> all = brandApplicationMapper.selectAll();
        PageInfo<TbBrandApplication> info = new PageInfo<TbBrandApplication>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbBrandApplication> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


	@Override
    public PageInfo<TbBrandApplication> findPage(Integer pageNo, Integer pageSize, TbBrandApplication brandApplication) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbBrandApplication.class);
        Example.Criteria criteria = example.createCriteria();

        if(brandApplication!=null){			
						if(StringUtils.isNotBlank(brandApplication.getName())){
				criteria.andLike("name","%"+brandApplication.getName()+"%");
				//criteria.andNameLike("%"+brandApplication.getName()+"%");
			}
			if(StringUtils.isNotBlank(brandApplication.getFirstChar())){
				criteria.andLike("firstChar","%"+brandApplication.getFirstChar()+"%");
				//criteria.andFirstCharLike("%"+brandApplication.getFirstChar()+"%");
			}
			if(StringUtils.isNotBlank(brandApplication.getSellerId())){
				criteria.andLike("sellerId","%"+brandApplication.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+brandApplication.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(brandApplication.getSellerName())){
				criteria.andLike("sellerName","%"+brandApplication.getSellerName()+"%");
				//criteria.andSellerNameLike("%"+brandApplication.getSellerName()+"%");
			}
			if(StringUtils.isNotBlank(brandApplication.getStatus())){
				criteria.andLike("status","%"+brandApplication.getStatus()+"%");
				//criteria.andStatusLike("%"+brandApplication.getStatus()+"%");
			}
	
		}
        List<TbBrandApplication> all = brandApplicationMapper.selectByExample(example);
        PageInfo<TbBrandApplication> info = new PageInfo<TbBrandApplication>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbBrandApplication> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	@Override
	public void updateStatus(String status, Long[] ids) {
		Example example = new Example(TbBrandApplication.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", Arrays.asList(ids));
		TbBrandApplication tbBrandApplication = new TbBrandApplication();
		tbBrandApplication.setStatus(status);
		brandApplicationMapper.updateByExampleSelective(tbBrandApplication,example);
	}

}
