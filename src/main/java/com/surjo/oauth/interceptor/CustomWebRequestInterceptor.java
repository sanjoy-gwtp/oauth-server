package com.surjo.oauth.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;


public class CustomWebRequestInterceptor implements WebRequestInterceptor {

    Logger logger = LoggerFactory.getLogger(CustomWebRequestInterceptor.class);

    @Override
    public void preHandle(WebRequest webRequest) throws Exception {
    }

    @Override
    public void postHandle(WebRequest webRequest, ModelMap modelMap) throws Exception {
    }

    @Override
    public void afterCompletion(WebRequest webRequest, Exception e) throws Exception {
    }
}
