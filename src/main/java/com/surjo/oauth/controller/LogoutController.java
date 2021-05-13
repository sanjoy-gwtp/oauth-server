package com.surjo.oauth.controller;

import com.surjo.oauth.service.impl.CustomTokenServices;
import com.surjo.oauth.session.UserSessionService;
import com.surjo.oauth.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LogoutController {

    Logger logger = LoggerFactory.getLogger(LogoutController.class);

    @Autowired
    CustomTokenServices tokenService;

    @Autowired
    UserSessionService userSessionService;


    @RequestMapping(method = RequestMethod.POST, value = "/logout/user")
    @ResponseBody
    public void revokeToken(HttpServletRequest request) {
        OAuth2AccessToken auth2AccessToken = null;
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            auth2AccessToken = tokenService.readAccessToken(token);
        }
        if(auth2AccessToken != null) {
            tokenService.revokeToken(auth2AccessToken.getValue());

            try {
                userSessionService.destroySession(TokenUtil.getJti(auth2AccessToken.getRefreshToken().getValue()));
            } catch (Exception e) {
                logger.warn("Failed to destroy session: {}", TokenUtil.getJti(auth2AccessToken.getRefreshToken().getValue()));
            }
        }
    }
//
//    @RequestMapping(method = RequestMethod.GET, value = "/tokens")
//    @ResponseBody
//    public List<String> getTokens() {
//        List<String> tokenValues = new ArrayList<String>();
//        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId("sampleClientId");
//        if (tokens != null) {
//            for (OAuth2AccessToken token : tokens) {
//                tokenValues.add(token.getValue());
//            }
//        }
//        return tokenValues;
//    }
//
//    @RequestMapping(method = RequestMethod.POST, value = "/tokens/revokeRefreshToken/{tokenId:.*}")
//    @ResponseBody
//    public String revokeRefreshToken(@PathVariable String tokenId) {
//        if (tokenStore instanceof JdbcTokenStore) {
//            ((JdbcTokenStore) tokenStore).removeRefreshToken(tokenId);
//        }
//        return tokenId;
//    }

}