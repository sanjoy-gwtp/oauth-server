package com.surjo.oauth.repository;


import com.surjo.oauth.entity.CredentialEntity;
import com.surjo.oauth.model.CredentialType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialRepository extends JpaRepository<CredentialEntity,Long> {
    CredentialEntity findOneByDeviceAndTypeAndUserUsername(String device, CredentialType type, String username);

    CredentialEntity findOneByTypeAndUserUsername(CredentialType type, String username);
}
