package com.pinyougou.manager.config;


import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class ManagerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //添加用户民 admin 密码:admin 角色:ADMIN
        auth.inMemoryAuthentication().withUser("admin").password("{noop}123").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**","/img/**","/js/**","/plugins/**","/login.html").permitAll()
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true)
                .failureUrl("/login/error");

        http.logout().logoutUrl("/logout").invalidateHttpSession(true);

        http.csrf().disable();

        //开启同源iframe 可以访问策略
        http.headers().frameOptions().sameOrigin();
    }
}
