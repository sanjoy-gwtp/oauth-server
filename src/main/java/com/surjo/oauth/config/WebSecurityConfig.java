package com.surjo.oauth.config;

import com.surjo.oauth.filter.PostLoginHttpSessionDestroyFilter;
import com.surjo.oauth.filter.SessionCookieFilter;
import com.surjo.oauth.provider.CustomAuthenticationProvider;
import com.surjo.oauth.service.impl.CustomTokenServices;
import com.surjo.oauth.service.impl.LoginAttemptService;
import com.surjo.oauth.session.UserSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.session.SessionManagementFilter;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${neekhut.auth.login.defaultRedirectUrl:@http://localhost:4201}")
    private String defaultLoginRedirectUrl;


    @Autowired
    private CustomizeLogoutSuccessHandler customizeLogoutSuccessHandler;


    @Autowired
    UserSessionService customUserSessionService;


    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    CustomTokenServices tokenService;

    @Autowired
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;



    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
        auth.authenticationProvider(preAuthenticatedAuthenticationProvider());
        auth.authenticationEventPublisher(authenticationEventPublisher());
    }

    @Bean
    public DefaultAuthenticationEventPublisher authenticationEventPublisher() {
        return new CustomAuthenticationEventPublisher();
    }


    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CustomAuthenticationProvider authenticationProvider() {
        CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        return authenticationProvider;
    }

    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean("preAuthenticationUserDetailsService")
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> preAuthenticationUserDetailsService() {
        UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> userDetailsService = new UserDetailsByNameServiceWrapper<>();
        userDetailsService.setUserDetailsService(userDetailsService());
        return userDetailsService;
    }

    @Bean("preAuthenticatedAuthenticationProvider")
    public AuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(preAuthenticationUserDetailsService());
        return preAuthenticatedAuthenticationProvider;
    }

    @Bean
    public UserSessionService userSessionService() {
        return new UserSessionService();
    }

    @Bean
    public SessionCookieFilter sessionCookieFilter(){
        return new SessionCookieFilter();
    }

    @Bean
    public PostLoginHttpSessionDestroyFilter postLoginHttpSessionDestroyFilter(){
        return new PostLoginHttpSessionDestroyFilter();
    }



    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http
                .addFilterAfter(sessionCookieFilter(),SessionManagementFilter.class).requestCache().disable()
                .addFilterAfter(postLoginHttpSessionDestroyFilter(),SessionManagementFilter.class).requestCache().disable()
                .authorizeRequests()
                .antMatchers("/login","/password/**","/sessions","/webjars/**","/logout/**","/sso/**").permitAll()
                .anyRequest().authenticated()

//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .successHandler(customAuthenticationSuccessHandler)
//                .failureHandler(new CustomAuthenticationFailureHandler())
//                .permitAll()
//
//                .and()
//                .logout()
//                .logoutUrl("/logout")
//                .logoutSuccessHandler(customizeLogoutSuccessHandler)
//                .permitAll()

                .and()
                .headers()
                .frameOptions()
                .disable()

                .and()
                .csrf().disable()
                .sessionManagement()
                .maximumSessions(1);
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring() .antMatchers("/**.js")
                .antMatchers("/**.css");

    }



}
