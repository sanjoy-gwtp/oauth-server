package com.surjo.oauth.repository;

import com.surjo.oauth.entity.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientDetailRepository extends JpaRepository<OauthClientDetails,Long> {
    Optional<OauthClientDetails> findOneByClientId(String clientId);
}
