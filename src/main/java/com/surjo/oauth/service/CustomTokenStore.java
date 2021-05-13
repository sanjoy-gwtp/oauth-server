package com.surjo.oauth.service;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.List;
import java.util.Set;

public interface CustomTokenStore {
//    List<OAuth2AccessToken> findTokensByUserName(String userName);

    void revokeTokenByUserName(String username, String sessionId, String clientId);

}