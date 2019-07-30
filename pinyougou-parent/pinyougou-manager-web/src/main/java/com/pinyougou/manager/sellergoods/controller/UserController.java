//package com.pinyougou.manager.sellergoods.controller;
//
//import com.alibaba.dubbo.config.annotation.Reference;
//import com.github.pagehelper.PageInfo;
//import com.pinyougou.POIUtils;
//import com.pinyougou.pojo.TbUser;
//import com.pinyougou.service.UserService;
//import entity.Result;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * controller
// *
// * @author Administrator
// */
//@RestController
//@RequestMapping("/user")
//public class UserController {
//
//    @Reference
//    private UserService userService;
//
//    @RequestMapping("/userExport")
//    public ResponseEntity<byte[]> userExport() {
//        try {
//            List<TbUser> users = userService.findAll();
//            byte[] body = POIUtils.exportExcel(users).toByteArray();
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "attachment; filename=user.xlsx");
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            return new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
//        }
//    }
//
//
//    /**
//     * 返回全部列表
//     *
//     * @return
//     */
//    @RequestMapping("/findAll")
//    public List<TbUser> findAll() {
//        return userService.findAll();
//    }
//
//
//    @RequestMapping("/findPage")
//    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
//                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
//        return userService.findPage(pageNo, pageSize);
//    }
//
//    /**
//     * 增加
//     *
//     * @param user
//     * @return
//     */
//    @RequestMapping("/add")
//    public Result add(@RequestBody TbUser user) {
//        try {
//            userService.add(user);
//            return new Result(true, "增加成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Result(false, "增加失败");
//        }
//    }
//
//    /**
//     * 修改
//     *
//     * @param user
//     * @return
//     */
//    @RequestMapping("/update")
//    public Result update(@RequestBody TbUser user) {
//        try {
//            userService.update(user);
//            return new Result(true, "修改成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Result(false, "修改失败");
//        }
//    }
//
//    /**
//     * 获取实体
//     *
//     * @param id
//     * @return
//     */
//    @RequestMapping("/findOne/{id}")
//    public TbUser findOne(@PathVariable(value = "id") Long id) {
//        return userService.findOne(id);
//    }
//
//
//
//    /**
//     * 批量删除
//     *
//     * @param ids
//     * @return
//     */
//    @RequestMapping("/delete")
//    public Result delete(@RequestBody Long[] ids) {
//        try {
//            userService.delete(ids);
//            return new Result(true, "删除成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new Result(false, "删除失败");
//        }
//    }
//
//
//    @RequestMapping("/search")
//    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
//                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
//                                     @RequestBody TbUser user) {
//        return userService.findPage(pageNo, pageSize, user);
//    }
//
//}
