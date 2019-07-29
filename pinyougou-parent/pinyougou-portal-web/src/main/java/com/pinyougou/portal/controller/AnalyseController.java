package com.pinyougou.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analyse")
public class AnalyseController {
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/user")
    public String user(){
        RedisAtomicLong count = new RedisAtomicLong("portalCount", redisTemplate.getConnectionFactory());
        count.incrementAndGet();
        return "Thanks";
    }
}
