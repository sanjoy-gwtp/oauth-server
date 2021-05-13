package com.surjo.oauth.repository;


import com.surjo.oauth.entity.PasswordPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordPolicyRepository extends JpaRepository<PasswordPolicyEntity, Long> {
    Optional<PasswordPolicyEntity> findById(long id);
    Optional<PasswordPolicyEntity> findByName(String name);
    boolean existsById(Long id);
}
