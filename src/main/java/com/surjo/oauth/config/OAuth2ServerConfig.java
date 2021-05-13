package com.surjo.oauth.config;


import com.surjo.oauth.exception.CustomOauthException;
import com.surjo.oauth.interceptor.CustomHandlerInterceptor;
import com.surjo.oauth.interceptor.CustomWebRequestInterceptor;
import com.surjo.oauth.model.User;
import com.surjo.oauth.provider.CustomClientDetailsProvider;
import com.surjo.oauth.service.impl.CustomTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;


import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter{


    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Qualifier("dataSource")
    @Autowired
    DataSource dataSource;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    
    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
       oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService());
	}


    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenServices(this.tokenServices())
                .tokenEnhancer(tokenEnhancer())
                .accessTokenConverter(accessTokenConverter())
                .tokenStore(redisTokenStore())
                .addInterceptor(webRequestInterceptor())
                .addInterceptor(handlerInterceptor())
                .userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager)
                .exceptionTranslator(exception -> {
                if(exception instanceof OAuth2Exception) {
                OAuth2Exception oAuth2Exception = (OAuth2Exception) exception;
                return ResponseEntity
                        .status(oAuth2Exception.getHttpErrorCode())
                        .body(new CustomOauthException(oAuth2Exception.getMessage(),oAuth2Exception));
            } else {
                throw exception;
            }
        });
    }


    @Bean
    public ClientDetailsService clientDetailsService() {
        return new CustomClientDetailsProvider();
    }

    @Bean
    public CustomTokenServices tokenServices() {
        CustomTokenServices tokenServices = new CustomTokenServices();
        tokenServices.setTokenStore(this.redisTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(false);
        tokenServices.setClientDetailsService(clientDetailsService());
        tokenServices.setTokenEnhancer(tokenEnhancer());
        tokenServices.setAuthenticationManager(authenticationManager);
        return tokenServices;
    }

    @Bean
    public TokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        boolean withAuthorities=true;
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(new DefaultAccessTokenConverter(){
            @Override
            public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
                if(withAuthorities) {
                    return super.convertAccessToken(token, authentication);
                } else {
                    if (authentication.isClientOnly()) {
                        return super.convertAccessToken(token, authentication);
                    } else {
                        Map<String, ?> response = super.convertAccessToken(token, authentication);
                        response.remove("authorities");
                        return response;
                    }
                }
            }
        });
        converter.setSigningKey("123");
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(
                        (oAuth2AccessToken, authentication) -> {
                            Map<String,Object> additionalInfo = new HashMap<>();
                            User user=(User) authentication.getPrincipal();
                            additionalInfo.put("firstName", user.getFirstName());
                            additionalInfo.put("lastName", user.getLastName());
                            additionalInfo.put("branch",user.getBranch());
                            additionalInfo.put("menuGroup",user.getMenuGroup());
                            additionalInfo.put("user_name",user.getUserName());
                            ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(additionalInfo);
                            return oAuth2AccessToken;
                        },
                        accessTokenConverter()
                )
        );
        return tokenEnhancerChain;
    }

    @Bean
    public CustomWebRequestInterceptor webRequestInterceptor(){
        return new CustomWebRequestInterceptor();
    }

    @Bean
    public CustomHandlerInterceptor handlerInterceptor(){
        return new CustomHandlerInterceptor();
    }

}
