package com.surjo.oauth.service;


import com.surjo.oauth.model.PasswordPolicy;

import java.util.List;
import java.util.Optional;

public interface PasswordPolicyService {

    PasswordPolicy createPasswordPolicy(PasswordPolicy passwordPolicy);

    void updatePasswordPolicy(PasswordPolicy passwordPolicy);

    void removePasswordPolicy(long id);

    Optional<PasswordPolicy> findPasswordPolicy(Long id);

    List<PasswordPolicy> findPasswordPolicies();

    boolean isExists(Long id);

}
