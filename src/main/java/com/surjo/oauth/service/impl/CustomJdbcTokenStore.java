//package com.surjo.oauth.service.impl;
//
//import User;
//import CustomTokenStore;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
//import org.springframework.stereotype.Service;
//
//import javax.sql.DataSource;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.stream.Collectors;
//
///**
// * Created by sanjoy on 9/24/18.
// */
//@Service
//public class CustomJdbcTokenStore extends JdbcTokenStore  implements CustomTokenStore{
//
//    Logger logger = LoggerFactory.getLogger(CustomJdbcTokenStore.class);
//
//
//    public CustomJdbcTokenStore(DataSource dataSource) {
//        super(dataSource);
//    }
//
//
//    @Override
//    public List<OAuth2AccessToken> findTokensByUserName(String userName){
//        return (List<OAuth2AccessToken>)super.findTokensByUserName(userName);
//    }
//
//    @Override
//    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
//        super.storeAccessToken(token, authentication);
//    }
//
//
//    @Override
//    public OAuth2AccessToken readAccessToken(String tokenValue) {
//        OAuth2AccessToken token = super.readAccessToken(tokenValue);
//
//        if(token == null) {
//            return null;
//        }
//        OAuth2Authentication authentication = readAuthentication(token);
//        if (token.getAdditionalInformation().get("authorities") == null) {
//
//            Collection<? extends GrantedAuthority> authorities = ((User) authentication.getPrincipal()).getAuthorities();
//
//            if (authorities != null) {
//                token.getAdditionalInformation().put("authorities", authorities
//                        .stream()
//                        .map(authority -> authority.getAuthority())
//                        .collect(Collectors.toList())
//                );
//            }
//        }
//        return token;
//    }
//}
