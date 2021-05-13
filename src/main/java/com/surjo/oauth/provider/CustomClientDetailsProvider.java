package com.surjo.oauth.provider;

import com.surjo.oauth.repository.ClientDetailRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.oauth2.config.annotation.builders.JdbcClientDetailsServiceBuilder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by sanjoy on 3/24/19.
 */

public class CustomClientDetailsProvider implements ClientDetailsService, ApplicationContextAware {

    @Autowired
    DataSource dataSource;

    @Autowired
    private ClientDetailRepository clientDetailRepository;



    private ClientDetailsService jdbcClientProvider;

    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        buildJdbcClientProvider();
    }

    private void buildJdbcClientProvider() {
        JdbcClientDetailsServiceBuilder builder = new JdbcClientDetailsServiceBuilder();
        builder.dataSource(dataSource);
        try {
            this.jdbcClientProvider = builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Set<String> getClientIds() {
        Set<String> clientIds = clientDetailRepository.findAll().stream().map(cd-> cd.getClientId()).collect(Collectors.toSet());
        return clientIds;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        ClientDetails clientDetails = null;
        ClientRegistrationException throwable = null;
            try {
                clientDetails = jdbcClientProvider.loadClientByClientId(clientId);
            } catch (ClientRegistrationException e) {
                throwable = e;
            } catch (Exception e) {
                throwable = new ClientRegistrationException(e.getMessage(),e);
            }
        if(clientDetails == null) {
            throw throwable;
        }
        return clientDetails;
    }
}
