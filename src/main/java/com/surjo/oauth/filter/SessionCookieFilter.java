package com.surjo.oauth.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class SessionCookieFilter extends GenericFilterBean {

    Logger logger = LoggerFactory.getLogger(SessionCookieFilter.class);

    private final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/sso/ssoctrl.html", "GET");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if(requestMatcher.matches((HttpServletRequest) servletRequest)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication instanceof UsernamePasswordAuthenticationToken && authentication.isAuthenticated()) {
                HttpSession session = ((HttpServletRequest) servletRequest).getSession(false);
                if (session != null) {
                    logger.info("Session interval: {}", session.getMaxInactiveInterval());
                    Map<String, String> clientState = (Map<String, String>) session.getAttribute("CLIENT_STATE");
                    if (clientState != null) {
                        clientState.entrySet().forEach(entry -> {

                            String cookieValue = String.format("%s/%s/%s",
                                    entry.getKey(),
                                    entry.getValue(),
                                    session.getId());

                            Cookie cookie = new Cookie("NG_SESSION", cookieValue);
                            cookie.setPath(String.format("%s?client_id=%s", ((HttpServletRequest) servletRequest).getServletPath(), entry.getKey()));
                            cookie.setMaxAge(session.getMaxInactiveInterval());

                            ((HttpServletResponse) servletResponse).addCookie(cookie);
                        });
                    }
                }
            }
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }
}
