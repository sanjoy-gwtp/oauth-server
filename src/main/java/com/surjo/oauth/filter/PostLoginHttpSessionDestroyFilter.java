package com.surjo.oauth.filter;

/**
 * Created by sanjoy on 6/9/19.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class PostLoginHttpSessionDestroyFilter extends GenericFilterBean {

    Logger logger = LoggerFactory.getLogger(PostLoginHttpSessionDestroyFilter.class);

    private final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/sso/ssoctrl.html", "GET");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(requestMatcher.matches((HttpServletRequest) servletRequest)) {
            HttpSession session = ((HttpServletRequest) servletRequest).getSession(false);
            if(session != null) {
                session.invalidate();
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
}

