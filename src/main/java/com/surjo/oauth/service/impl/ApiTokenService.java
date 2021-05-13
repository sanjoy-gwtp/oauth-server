package com.surjo.oauth.service.impl;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sanjoy on 6/13/19.
 */

@Service
public class ApiTokenService {


    private AuthenticationManager authenticationManager;
    private OAuth2RequestFactory requestFactory;


    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap(tokenRequest.getRequestParameters());
        String username = (String)parameters.get("username");
        String password = (String)parameters.get("password");
        parameters.remove("password");
        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken)userAuth).setDetails(parameters);

        Authentication userAuth1;
        try {
            userAuth1 = this.authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException var8) {
            throw new InvalidGrantException(var8.getMessage());
        } catch (BadCredentialsException var9) {
            throw new InvalidGrantException(var9.getMessage());
        }

        if(userAuth1 != null && userAuth1.isAuthenticated()) {
            OAuth2Request storedOAuth2Request = this.requestFactory.createOAuth2Request(client, tokenRequest);
            return new OAuth2Authentication(storedOAuth2Request, userAuth1);
        } else {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }
    }
}
