package com.surjo.oauth.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class CustomHandlerInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(CustomHandlerInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        HttpSession session = httpServletRequest.getSession(false);
        logger.info("H_PP_S: '{}'",(session == null)?"?":session.getId());
        if(o instanceof HandlerMethod && ((HandlerMethod)o).getBeanType().equals(AuthorizationEndpoint.class)) {
            String client_id = httpServletRequest.getParameter("client_id");
            String state = httpServletRequest.getParameter("state");

            Map<String,String> clientState = (Map<String, String>) session.getAttribute("CLIENT_STATE");

            if(clientState == null) {
                clientState = new HashMap<>();
                session.setAttribute("CLIENT_STATE",clientState);
            }

            clientState.put(client_id,state);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
