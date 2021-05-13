package com.surjo.oauth.service.impl;

import com.surjo.oauth.util.HttpRequestUtil;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by sanjoy on 9/24/18.
 */
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

    private String terminal;
    private String branch;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.terminal = HttpRequestUtil.getTerminal(request);
        this.branch = request.getParameter("branchId");
    }

    public String getTerminal() {
        return terminal;
    }

    @Override
    public String getRemoteAddress() {
        return getTerminal();
    }

    public String getBranch() {
        return branch;
    }
}
