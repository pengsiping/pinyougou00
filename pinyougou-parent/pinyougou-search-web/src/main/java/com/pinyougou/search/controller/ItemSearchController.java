package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;


    @RequestMapping("/search")
    public Map<String,Object> search(@RequestBody Map<String,Object> map){
        if(map==null){
            map = new HashMap<>();
        }
        return itemSearchService.search(map);
    }

}
