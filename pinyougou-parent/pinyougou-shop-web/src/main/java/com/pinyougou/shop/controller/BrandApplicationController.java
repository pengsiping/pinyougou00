package com.pinyougou.shop.controller;
import java.util.List;
import java.util.Set;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrandApplication;
import com.pinyougou.sellergoods.service.BrandApplicationService;

import com.github.pagehelper.PageInfo;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/brandApplication")
public class BrandApplicationController {

	@Reference
	private BrandApplicationService brandApplicationService;

	@Reference
	private BrandService brandService;

	@Reference
	private SellerService sellerService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbBrandApplication> findAll(){			
		return brandApplicationService.findAll();
	}


	@RequestMapping("/findBrandByFirstChar")
	public List<TbBrand> findBrandByFirstChar(String firstChar) {
		return brandService.findBrandByFirstChar(firstChar);

	}
	@RequestMapping("/findFirstChar")
	public Set<String> findFirstChar() {
		return brandService.findFirstChar();
	}
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbBrandApplication> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return brandApplicationService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param brandApplication
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbBrandApplication brandApplication){
		try {
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			TbSeller tbSeller = sellerService.selectByPrimaryKey(name);
			brandApplication.setSellerId(name);
			brandApplication.setSellerName(tbSeller.getName());
			brandApplication.setStatus("0");
			brandApplicationService.add(brandApplication);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param brandApplication
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbBrandApplication brandApplication){
		try {
			brandApplicationService.update(brandApplication);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbBrandApplication findOne(@PathVariable(value = "id") Long id){
		return brandApplicationService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			brandApplicationService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbBrandApplication> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbBrandApplication brandApplication) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		brandApplication.setSellerId(name);
		System.out.println(brandApplication);
        return brandApplicationService.findPage(pageNo, pageSize, brandApplication);
    }
	
}
