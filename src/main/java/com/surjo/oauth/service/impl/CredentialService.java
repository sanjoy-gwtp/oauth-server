package com.surjo.oauth.service.impl;


import com.surjo.oauth.entity.CredentialEntity;
import com.surjo.oauth.entity.PasswordPolicyEntity;
import com.surjo.oauth.entity.UserEntity;
import com.surjo.oauth.exception.InvalidCredential;
import com.surjo.oauth.model.*;
import com.surjo.oauth.repository.CredentialRepository;
import com.surjo.oauth.repository.PasswordPolicyRepository;
import com.surjo.oauth.repository.UserRepository;
import com.surjo.oauth.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;



@Service
public class CredentialService {

    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final PasswordPolicyRepository passwordPolicyRepository;

    public CredentialService(UserRepository userRepository,
                             CredentialRepository credentialRepository, PasswordPolicyRepository passwordPolicyRepository) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.passwordPolicyRepository = passwordPolicyRepository;
    }

    public void resetCredential(ResetCredential credential) {

        if(!credential.getValue().endsWith(credential.getConfirmNewValue())) {
            throw new RuntimeException("Credential do not match");
        }

        CredentialEntity credentialEntity = findCredentialOrCreate(credential);

        updateCredential(credential, credentialEntity);
        credentialEntity.setExpireDate(LocalDate.now());
        credentialRepository.save(credentialEntity);
    }

    public Boolean changeCredential(ChangeCredential credential) {
        if(!credential.getNewValue().equals(credential.getConfirmNewValue())) {
            throw new RuntimeException("New credential do not match");
        }

        CredentialEntity credentialEntity = findCredential(credential);

        if(matchCredential(
                credentialEntity.getUser().getUsername()+":"+credential.getNewValue(),
                credentialEntity.getValue(),
                credentialEntity.getSalt())) {
            throw new RuntimeException("New password and current password must not be same.");
        }

        if(!matchCredential(
                credentialEntity.getUser().getUsername()+":"+credential.getValue(),
                credentialEntity.getValue(),
                credentialEntity.getSalt())) {
            throw new InvalidCredential();
        }

        updateCredential(credential, credentialEntity);
        credentialEntity.setExpireDate(LocalDate.now().plusDays(getPasswordExpirationDays()));
        credentialRepository.save(credentialEntity);
        return true;
    }

    public void createCredential(Credential credential) {
        UserEntity user = userRepository.findByUsername(credential.getUsername()).orElse(null);
        CredentialEntity credentialEntity = new CredentialEntity();
        credentialEntity.setUser(user);
        updateCredential(credential, credentialEntity);
        credentialEntity.setExpireDate(LocalDate.now());
        credentialRepository.save(credentialEntity);
    }

    private boolean matchCredential(String value, String encValue, byte[] salt) {
        return  generateValueHash(value, salt, CredentialAlgorithm.MD5).equals(encValue);
    }

    private void updateCredential(Credential credential, CredentialEntity credentialEntity) {
        byte[] salt = PasswordUtil.generateSalt();
        String credentialValue = (credential instanceof ChangeCredential) ? ((ChangeCredential) credential).getNewValue() : credential.getValue();
        credentialEntity.setAlgorithm(CredentialAlgorithm.MD5);
        credentialEntity.setDevice(credential.getDevice());
        credentialEntity.setType(credential.getType());
        credentialEntity.setSalt(salt);
        System.out.println(credentialEntity.getUser().getUsername()+","+credentialValue);
        credentialEntity.setValue(generateValueHash(credentialEntity.getUser().getUsername() + ":" + credentialValue, salt, credentialEntity.getAlgorithm()
        ));
    }

    private CredentialEntity findCredential(Credential credential) {
        CredentialEntity credentialEntity;
        if(credential.getDevice() != null) {
            credentialEntity = credentialRepository.findOneByDeviceAndTypeAndUserUsername(credential.getDevice(), credential.getType(), credential.getUsername());
        } else {
            credentialEntity = credentialRepository.findOneByTypeAndUserUsername(credential.getType(), credential.getUsername());
        }
        return credentialEntity;
    }


    private CredentialEntity findCredentialOrCreate(Credential credential) {
        CredentialEntity credentialEntity = findCredential(credential);
        if(credentialEntity != null) return credentialEntity;
        UserEntity user = userRepository.findByUsername(credential.getUsername()).orElse(null);
        credentialEntity = new CredentialEntity();
        if(user == null) {
            throw new InvalidCredential();
        }
        credentialEntity.setUser(user);
        return credentialEntity;
    }

    private String generateValueHash(String value, byte[] salt, CredentialAlgorithm algorithm) {
        if(algorithm == CredentialAlgorithm.MD5) {
            return PasswordUtil.generateMD5(value, salt);
        }
        return null;
    }
    private Long getPasswordExpirationDays(){
        PasswordPolicyEntity policyEntity = passwordPolicyRepository.findByName("EXPIRY_PASSWORD").orElse(null);
        String days = policyEntity != null ? policyEntity.getValue() : "90";
        return Long.parseLong(days);
    }

    public boolean forgotCredential(Credential credential) {
        CredentialEntity credentialEntity = findCredential(credential);
        updateCredential(credential, credentialEntity);
        credentialEntity.setExpireDate(LocalDate.now().plusDays(getPasswordExpirationDays()));
        credentialRepository.save(credentialEntity);
        return true;
    }
}
