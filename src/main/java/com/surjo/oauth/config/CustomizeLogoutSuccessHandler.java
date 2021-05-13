package com.surjo.oauth.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomizeLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {


    @Value("${auth.logout.defaultRedirectUrl:@null}")
    private String defaultRedirectUrl;

    @PostConstruct
    public void init() {
        if(!"@null".equals(defaultRedirectUrl)) {
            setDefaultTargetUrl(defaultRedirectUrl);
        } else {
            setTargetUrlParameter("redirect_uri");
        }
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        super.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        if(request.getParameter(getTargetUrlParameter()) == null && getDefaultTargetUrl() != null) {
            return getDefaultTargetUrl();
        }
        return super.determineTargetUrl(request, response);
    }

}
