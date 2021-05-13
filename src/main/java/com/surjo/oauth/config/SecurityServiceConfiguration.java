package com.surjo.oauth.config;



import com.surjo.oauth.repository.CredentialRepository;
import com.surjo.oauth.repository.PasswordPolicyRepository;
import com.surjo.oauth.repository.UserRepository;
import com.surjo.oauth.service.PasswordPolicyService;
import com.surjo.oauth.service.impl.CredentialService;
import com.surjo.oauth.service.impl.PasswordPolicyServiceImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(basePackages = "com.surjo.oauth.repository")
@EntityScan(basePackages = "com.surjo.oauth.entity")
@Configuration
public class SecurityServiceConfiguration {

    @Bean
    public CredentialService credentialService(
            UserRepository userRepository,
            CredentialRepository credentialRepository,
            PasswordPolicyRepository passwordPolicyRepository) {
        return new CredentialService(userRepository,credentialRepository,passwordPolicyRepository);
    }

    @Bean
    public PasswordPolicyService passwordPolicyService(PasswordPolicyRepository passwordPolicyRepository) {
        return new PasswordPolicyServiceImpl(passwordPolicyRepository);
    }

//    @Bean
//    public PasswordValidator passwordValidator(PasswordPolicyService passwordPolicyService) {
//        return  new PasswordValidator(passwordPolicyService);
//    }

}
