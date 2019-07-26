package com.pinyougou.upload.controller;

import com.pinyougou.FastDFSClient;
import entity.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/upload")
public class UploadController {

    @RequestMapping("/uploadFile")
    @CrossOrigin(origins = {"http://localhost:9101","http://localhost:9102"},allowCredentials = "true")
    public Result upload(@RequestParam(value = "file")MultipartFile file){
        try {
            System.out.println("lianjie");
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs_client.conf");
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String path = fastDFSClient.uploadFile(bytes, extName);
            String realPath = "http://192.168.25.133/"+path;

            return new Result(true,realPath);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }
}
