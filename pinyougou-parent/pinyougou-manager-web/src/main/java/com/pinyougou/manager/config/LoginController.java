package com.pinyougou.manager.config;


import com.pinyougou.pojo.TbUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getLoginName")
    public String getLoginName(){
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
