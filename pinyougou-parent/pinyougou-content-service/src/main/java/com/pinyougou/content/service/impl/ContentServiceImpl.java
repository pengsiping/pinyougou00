package com.pinyougou.content.service.impl;
import java.util.Arrays;
import java.util.List;

import com.pinyougou.SysConstants;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;  

import com.pinyougou.content.service.ContentService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl extends CoreServiceImpl<TbContent>  implements ContentService {


	private TbContentMapper contentMapper;

	@Autowired
	public ContentServiceImpl(TbContentMapper contentMapper) {
		super(contentMapper, TbContent.class);
		this.contentMapper=contentMapper;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	
	

	
	@Override
    public PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbContent> all = contentMapper.selectAll();
        PageInfo<TbContent> info = new PageInfo<TbContent>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContent> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbContent> findPage(Integer pageNo, Integer pageSize, TbContent content) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();

        if(content!=null){			
						if(StringUtils.isNotBlank(content.getTitle())){
				criteria.andLike("title","%"+content.getTitle()+"%");
				//criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(StringUtils.isNotBlank(content.getUrl())){
				criteria.andLike("url","%"+content.getUrl()+"%");
				//criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(StringUtils.isNotBlank(content.getPic())){
				criteria.andLike("pic","%"+content.getPic()+"%");
				//criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(StringUtils.isNotBlank(content.getContent())){
				criteria.andLike("content","%"+content.getContent()+"%");
				//criteria.andContentLike("%"+content.getContent()+"%");
			}
			if(StringUtils.isNotBlank(content.getStatus())){
				criteria.andLike("status","%"+content.getStatus()+"%");
				//criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
        List<TbContent> all = contentMapper.selectByExample(example);
        PageInfo<TbContent> info = new PageInfo<TbContent>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbContent> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	@Override
	public List<TbContent> findAllCategory(Long categoryId) {
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps(SysConstants.CONTENT_REDIS_KEY2).get(categoryId);
		if(contentList!=null&&contentList.size()>0){
			return contentList;
		}
		TbContent tbContent = new TbContent();
		tbContent.setCategoryId(categoryId);
		tbContent.setStatus("1");
		List<TbContent> tbContents = contentMapper.select(tbContent);
		redisTemplate.boundHashOps(SysConstants.CONTENT_REDIS_KEY2).put(categoryId,tbContents);
		return tbContents;
	}

}
