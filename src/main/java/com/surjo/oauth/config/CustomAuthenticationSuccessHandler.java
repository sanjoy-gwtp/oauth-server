package com.surjo.oauth.config;

import com.surjo.oauth.service.impl.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sanjoy on 4/17/20.
 */
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Value("${neekhut.auth.login.defaultRedirectUrl:@http://localhost:4201}")
    private String defaultLoginRedirectUrl;

    @Autowired
    LoginAttemptService loginAttemptService;

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
//        loginAttemptService.loginSucceeded(((User) authentication.getPrincipal()).getUserName(),
//                ((org.springframework.security.web.authentication.WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        if((savedRequest == null
                || savedRequest.getRedirectUrl() == null
                || request.getRequestURL().indexOf(savedRequest.getRedirectUrl()) != -1)
                && "@null".equals(defaultLoginRedirectUrl) == false) {
            this.getRedirectStrategy().sendRedirect(request,response,defaultLoginRedirectUrl);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

}
