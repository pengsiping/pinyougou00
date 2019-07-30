package com.pinyougou.manager.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.POIUtils;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import entity.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

	@Reference
	private ItemCatService itemCatService;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbItemCat> findAll(){
		return itemCatService.findAll();
	}

	@RequestMapping("/template")
	public ResponseEntity<byte[]> template() {
		try {
			List<TbItemCat> list = new ArrayList<>();
			list.add(new TbItemCat());
			byte[] body = POIUtils.exportExcel(list).toByteArray();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=Specification.xlsx");
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			return new ResponseEntity<>(body, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
		}
	}

	@RequestMapping("/upload")
	public Result upload(@RequestParam("file") MultipartFile file){
		try {
			List<TbItemCat> list = POIUtils.readExcel(file.getInputStream(),file.getOriginalFilename(),TbItemCat.class);
			if(list.size() > 0){
				for (TbItemCat entity : list) {
					itemCatService.insertSelective(entity);
				}
			}
			return new Result(true,"导入成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,e.getMessage());
		}
	}

	@RequestMapping("/findPage")
    public PageInfo<TbItemCat> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return itemCatService.findPage(pageNo, pageSize);
    }

	/**
	 * 增加
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbItemCat itemCat){
		try {
			itemCatService.add(itemCat);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 修改
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbItemCat itemCat){
		try {
			itemCatService.update(itemCat);
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
	public TbItemCat findOne(@PathVariable(value = "id") Long id){
		return itemCatService.findOne(id);
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			itemCatService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}



	@RequestMapping("/search")
    public PageInfo<TbItemCat> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbItemCat itemCat) {
        return itemCatService.findPage(pageNo, pageSize, itemCat);
    }

    @RequestMapping("/findByParentId/{parentId}")
    public List<TbItemCat> findParentId(@PathVariable(value="parentId") Long parentId){
		List<TbItemCat> tbItemCats = itemCatService.findByParentId(parentId);
		return tbItemCats;
	}

}
