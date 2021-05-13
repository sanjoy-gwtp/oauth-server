package com.surjo.oauth.service.impl;

import com.surjo.oauth.config.CustomUserDetailsService;
import com.surjo.oauth.model.User;
import com.surjo.oauth.service.CustomTokenStore;
import com.surjo.oauth.util.TokenUtil;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomRedisTokenStore extends RedisTokenStore implements CustomTokenStore {

    public CustomRedisTokenStore(RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        super.storeAccessToken(token, authentication);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken token = super.readAccessToken(tokenValue);

        if(token == null) return null;

        OAuth2Authentication authentication = readAuthentication(token);

        if (token.getAdditionalInformation().get("authorities") == null) {


            if(authentication.getPrincipal() instanceof CustomUserDetailsService) {
                Collection<? extends GrantedAuthority> authorities =
                        ((User) authentication.getPrincipal()).getAuthorities();

                if (authorities != null) {
                    token.getAdditionalInformation().put("authorities", authorities
                            .stream()
                            .map(authority -> authority.getAuthority())
                            .collect(Collectors.toList())
                    );
                }
            }
        }

        return token;
    }

    @Override
    public void revokeTokenByUserName(String username, String sessionId, String clientId) {
        Collection<OAuth2AccessToken> tokens = super.findTokensByClientIdAndUserName(clientId,username);
        for (OAuth2AccessToken token:tokens){
            if(TokenUtil.getJti(token.getRefreshToken().getValue()).equals(sessionId)) {
                super.removeAccessToken(token.getValue());
                super.removeRefreshToken(token.getRefreshToken().getValue());
            }
        }

    }
}
