package com.surjo.oauth.config;

import com.surjo.oauth.model.User;
import com.surjo.oauth.service.impl.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

/**
 * Created by sanjoy on 4/17/20.
 */
public class CustomAuthenticationEventPublisher extends DefaultAuthenticationEventPublisher {

    @Autowired
    LoginAttemptService loginAttemptService;

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        //super.publishAuthenticationSuccess(authentication);
        if(authentication.getPrincipal() instanceof User){
            loginAttemptService.loginSucceeded(((User) authentication.getPrincipal()).getUserName(),"");
                   // ((org.springframework.security.web.authentication.WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        }else {
            loginAttemptService.loginSucceeded((String) authentication.getPrincipal(), "231.232.10.10");
        }
    }


    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        //super.publishAuthenticationFailure(exception, authentication);

        if(exception instanceof CredentialsExpiredException) {
            System.out.println("CredentialsExpiredException");
        }
        else if(exception instanceof BadCredentialsException){
            System.out.println("BadCredentialsException");
            if(authentication.getPrincipal() instanceof User){
                loginAttemptService.loginFailed(((User) authentication.getPrincipal()).getUserName(),"");
                        //((org.springframework.security.web.authentication.WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
            }else {
                loginAttemptService.loginFailed((String)authentication.getPrincipal(),
                        "231.232.10.10");
            }

        }
        else if(exception instanceof UsernameNotFoundException){
            System.out.println("UsernameNotFoundException");
        }
        else if(exception instanceof LockedException){
            System.out.println("LockedException");
        }
        else if(exception instanceof AccountExpiredException){
            System.out.println("AccountExpiredException");
        }
        else if (exception instanceof SessionAuthenticationException){
            System.out.println("SessionAuthenticationException");
        }
    }


}
