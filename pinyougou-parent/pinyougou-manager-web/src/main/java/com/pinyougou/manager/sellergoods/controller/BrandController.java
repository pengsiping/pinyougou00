//package com.pinyougou.manager.sellergoods.controller;
//
//import com.alibaba.dubbo.config.annotation.Reference;
//import com.github.pagehelper.PageInfo;
//import com.pinyougou.POIUtils;
//import com.pinyougou.pojo.TbBrand;
//import com.pinyougou.sellergoods.service.BrandService;
//import entity.Result;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * controller
// * @author Administrator
// *
// */
//@RestController
//@RequestMapping("/brand")
//public class BrandController {
//
//	@Reference
//	private BrandService brandService;
//
//	/**
//	 * 返回全部列表
//	 * @return
//	 */
//	@RequestMapping("/findAll")
//	public List<TbBrand> findAll(){
//		return brandService.findAll();
//	}
//
//	@RequestMapping("/template")
//	public ResponseEntity<byte[]> template() {
//		try {
//			List<TbBrand> list = new ArrayList<>();
//			list.add(new TbBrand());
//			byte[] body = POIUtils.exportExcel(list).toByteArray();
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Disposition", "attachment; filename=brand.xlsx");
//			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//			return new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
//		}
//	}
//
//	@RequestMapping("/upload")
//	public Result upload(@RequestParam("file") MultipartFile file){
//		try {
//			List<TbBrand> list = POIUtils.readExcel(file.getInputStream(),file.getOriginalFilename(),TbBrand.class);
//			if(list.size() > 0){
//				for (TbBrand tbBrand : list) {
//					brandService.insertSelective(tbBrand);
//				}
//			}
//			return new Result(true,"导入成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new Result(false,e.getMessage());
//		}
//	}
//
//	@RequestMapping("/findPage")
//    public PageInfo<TbBrand> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
//                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
//        return brandService.findPage(pageNo, pageSize);
//    }
//
//	/**
//	 * 增加
//	 * @param brand
//	 * @return
//	 */
//	@RequestMapping("/add")
//	public Result add(@RequestBody TbBrand brand){
//		try {
//			brandService.add(brand);
//			return new Result(true, "增加成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new Result(false, "增加失败");
//		}
//	}
//
//	/**
//	 * 修改
//	 * @param brand
//	 * @return
//	 */
//	@RequestMapping("/update")
//	public Result update(@RequestBody TbBrand brand){
//		try {
//			brandService.update(brand);
//			return new Result(true, "修改成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new Result(false, "修改失败");
//		}
//	}
//
//	/**
//	 * 获取实体
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping("/findOne/{id}")
//	public TbBrand findOne(@PathVariable(value = "id") Long id){
//		return brandService.findOne(id);
//	}
//
//	/**
//	 * 批量删除
//	 * @param ids
//	 * @return
//	 */
//	@RequestMapping("/delete")
//	public Result delete(@RequestBody Long[] ids){
//		try {
//			brandService.delete(ids);
//			return new Result(true, "删除成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new Result(false, "删除失败");
//		}
//	}
//
//
//
//	@RequestMapping("/search")
//    public PageInfo<TbBrand> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
//                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
//                                      @RequestBody TbBrand brand) {
//        return brandService.findPage(pageNo, pageSize, brand);
//    }
//
//}
