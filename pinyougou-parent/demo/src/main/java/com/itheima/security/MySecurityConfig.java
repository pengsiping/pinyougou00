package com.itheima.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       //定义一个用户名admin 密码 admin 并拥有admin角色的用户
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //授权 登录和错误页面 不需要登录
        //其他的请求/admin/** 都需要拥有ADMIN的角色的人 才可以访问
        //其他的请求/user/** 都需要拥有USER的角色的人 才可以访问
        //其他的任意请求 都只要登录就可以访问了。
        http.authorizeRequests()
                .antMatchers("/login.html","/error.html"). permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated();
        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index.html",true)
                .failureUrl("/error.html");
        //禁用Csrf
        http.csrf().disable();

        //super.configure(http);
    }
}
