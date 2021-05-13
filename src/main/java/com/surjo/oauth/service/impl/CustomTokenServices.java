package com.surjo.oauth.service.impl;



import com.surjo.oauth.model.User;
import com.surjo.oauth.provider.CustomClientDetailsProvider;
import com.surjo.oauth.service.CustomTokenStore;
import com.surjo.oauth.session.UserSessionService;
import com.surjo.oauth.util.HttpRequestUtil;
import com.surjo.oauth.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;



public class CustomTokenServices extends DefaultTokenServices{

    Logger logger = LoggerFactory.getLogger(CustomTokenServices.class);


    @Autowired
    private CustomTokenStore tokenStore;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private CustomClientDetailsProvider clientDetailsService;



    @Override
    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        super.setClientDetailsService(clientDetailsService);
        this.clientDetailsService = (CustomClientDetailsProvider) clientDetailsService;
    }

    @Override
    public boolean revokeToken(String tokenValue) {
        return super.revokeToken(tokenValue);
    }


    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OAuth2AccessToken token = super.createAccessToken(authentication);
        onNewToken(token);
        return token;
    }

    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {

        String expiredSessionId = TokenUtil.getJti(refreshTokenValue);

        OAuth2AccessToken token = super.refreshAccessToken(refreshTokenValue, tokenRequest);
        onNewToken(token);

        try {
            if (expiredSessionId != null) {
                userSessionService.destroySession(expiredSessionId);
            }
        } catch (Exception e) {
            logger.warn("Failed to update session: {}", expiredSessionId);
        }

        return token;
    }

    private void onNewToken(OAuth2AccessToken token) {

        //Long branch = (Long) token.getAdditionalInformation().get("activeBranch");
        String terminal = HttpRequestUtil.getTerminal(httpServletRequest);
        OAuth2Authentication authentication = loadAuthentication(token.getValue());

        if(authentication.getPrincipal() instanceof User) {
            Set<String> authorities = (authentication.getAuthorities() != null)
                    ? authentication.getAuthorities().stream()
                    .map(ga -> ga.getAuthority())
                    .collect(Collectors.toSet())
                    : new HashSet<>();

            token.getAdditionalInformation().put("authorities", authorities );
            User user=((User) authentication.getPrincipal());
            userSessionService.createUserSession(
                    authentication.getName(),
                    user.getUserFullName(),
                    TokenUtil.getJti(token.getRefreshToken().getValue()),
                    authentication.getOAuth2Request().getClientId(),
                    terminal, user.getBranch() == null ? 0 : user.getBranch(), token.getRefreshToken().getValue(),
                    new Date().getTime(),
                    TokenUtil.getExpireTime(token.getRefreshToken().getValue())
            );
        }
    }

    @Override
    public void setTokenStore(TokenStore tokenStore) {
        super.setTokenStore(tokenStore);
        if (tokenStore instanceof CustomTokenStore) {
            this.tokenStore = (CustomTokenStore) tokenStore;
        }
    }

    public List<OAuth2AccessToken> getAccessTokens(String clientId, String username) {
        List<OAuth2AccessToken> tokens = new ArrayList<>();
        Set<String> clientIds = new HashSet<>();

        if (clientId == null) {
            clientIds = clientDetailsService.getClientIds();
        } else {
            clientIds.add(clientId);
        }

        clientIds.forEach(cid -> {
            if (username != null) {
                tokens.addAll(((TokenStore) tokenStore).findTokensByClientIdAndUserName(cid, username));
            } else {
                tokens.addAll(((TokenStore) tokenStore).findTokensByClientId(cid));
            }
        });
        return tokens;
    }

    public OAuth2Authentication getAuthentication(OAuth2AccessToken accessToken) {
        return ((TokenStore) tokenStore).readAuthentication(accessToken);
    }

    public Collection<OAuth2Authentication> getAuthentications(List<OAuth2AccessToken> auth2AccessTokens) {

        List<OAuth2Authentication> authentications = new ArrayList<>();

        for (OAuth2AccessToken token : auth2AccessTokens) {
            OAuth2Authentication auth = getAuthentication(token);
            if (auth != null) {
                authentications.add(auth);
            }
        }

        return authentications;
    }

    public boolean revokeByRefreshToken(String tokenValue) {
        if(tokenValue == null) return false;

        OAuth2RefreshToken refreshToken = ((TokenStore)tokenStore).readRefreshToken(tokenValue);

        if(refreshToken != null) {
            ((TokenStore) tokenStore).removeAccessTokenUsingRefreshToken(refreshToken);
            ((TokenStore) tokenStore).removeRefreshToken(refreshToken);
        }

        return true;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException, InvalidTokenException {
        return super.loadAuthentication(accessTokenValue);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        return super.readAccessToken(accessToken);
    }

}

