package com.pinyougou.demo.service;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException  {
        System.out.println("----------------------------username"+s);
        return new User(s,"", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN"));
    }
}
