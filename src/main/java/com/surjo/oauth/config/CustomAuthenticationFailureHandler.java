package com.surjo.oauth.config;

import com.surjo.oauth.service.impl.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by sanjoy on 4/17/20.
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    LoginAttemptService loginAttemptService;

        private String defaultFailureUrl;


        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException exception) throws IOException, ServletException {


            String failureUrl = getFailureUrl(request, exception);


//            if (exception instanceof BadCredentialsException) {
//                System.out.println(exception.getLocalizedMessage());
//                loginAttemptService.loginFailed(request.getParameter("username"), request.getRemoteHost());
//            }

            if (failureUrl == null) {
                logger.debug("No failure URL set, sending 401 Unauthorized error");
                response.sendError(HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase());
            } else {
                _saveException(request, exception);

                if (isUseForward()) {
                    logger.debug("Forwarding to " + failureUrl);
                    request.getRequestDispatcher(failureUrl)
                            .forward(request, response);
                } else {
                    logger.debug("Redirecting to " + failureUrl);
                    getRedirectStrategy().sendRedirect(request, response, failureUrl);
                }
            }
        }

        private String getFailureUrl(HttpServletRequest request, AuthenticationException exception) {
            if (exception instanceof CredentialsExpiredException) {
                return "/password?warn";
            }
            else if(exception instanceof DisabledException) {
                return "/login?error=1000";
            }
            else if (exception instanceof BadCredentialsException) {
                return "/login?error=1001";
            }
            else if (exception instanceof UsernameNotFoundException) {
                return "/login?error=1002";
            }
            else if (exception instanceof LockedException) {
                return "/login?error=1003";
            }
            else if (exception instanceof AccountExpiredException) {
                return "/login?error=1004";
            }
            else if (exception instanceof SessionAuthenticationException) {
                return "/login?error=1005";
            } else {
                return defaultFailureUrl;
            }
        }

        private void _saveException(HttpServletRequest request, AuthenticationException exception) {

            if (exception instanceof CredentialsExpiredException) {
                if (isUseForward()) {
                    request.setAttribute("USER_PRINCIPLE", request.getParameter("username"));
                } else {
                    HttpSession session = request.getSession(false);
                    if (session != null && isAllowSessionCreation()) {
                        session.setAttribute("USER_PRINCIPLE", request.getParameter("username"));
                    }
                }
            }

            saveException(request, exception);
        }

        @Override
        public void setDefaultFailureUrl(String defaultFailureUrl) {
            super.setDefaultFailureUrl(defaultFailureUrl);
            this.defaultFailureUrl = defaultFailureUrl;
        }
}
