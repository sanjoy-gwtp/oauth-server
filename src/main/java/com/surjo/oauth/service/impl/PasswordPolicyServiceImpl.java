package com.surjo.oauth.service.impl;



import com.surjo.oauth.model.PasswordPolicy;
import com.surjo.oauth.entity.PasswordPolicyEntity;
import com.surjo.oauth.repository.PasswordPolicyRepository;
import com.surjo.oauth.service.PasswordPolicyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PasswordPolicyServiceImpl implements PasswordPolicyService {
    private final PasswordPolicyRepository passwordPolicyRepository;

    public PasswordPolicyServiceImpl(PasswordPolicyRepository passwordPolicyRepository) {
        this.passwordPolicyRepository = passwordPolicyRepository;
    }

    @Override
    public PasswordPolicy createPasswordPolicy(PasswordPolicy passwordPolicy) {
        return toDomain(passwordPolicyRepository.save(toEntity(passwordPolicy)));
    }

    @Override
    public void updatePasswordPolicy(PasswordPolicy passwordPolicy) {
        if(passwordPolicy.getId() == null)
            throw new IllegalArgumentException("Unsaved entity sent for update");
        passwordPolicyRepository.save(toEntity(passwordPolicy));
    }

    @Override
    public void removePasswordPolicy(long id) {
        passwordPolicyRepository.deleteById(id);
    }

    @Override
    public Optional<PasswordPolicy> findPasswordPolicy(Long id) {
        return passwordPolicyRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<PasswordPolicy> findPasswordPolicies() {
        List<PasswordPolicyEntity> passwordPolicy = passwordPolicyRepository.findAll();
        return passwordPolicy.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean isExists(Long id) {
        return passwordPolicyRepository.existsById(id);
    }

    private PasswordPolicyEntity toEntity(PasswordPolicy domain) {
        PasswordPolicyEntity passwordPolicyEntity = new PasswordPolicyEntity();

        passwordPolicyEntity.setId(domain.getId());
        passwordPolicyEntity.setName(domain.getName());
        passwordPolicyEntity.setValue(domain.getValue());
        passwordPolicyEntity.setCaption(domain.getCaption());
        passwordPolicyEntity.setDataType(domain.getDataType());
        passwordPolicyEntity.setDescription(domain.getDescription());
        passwordPolicyEntity.setValues_(domain.getValues());
        return passwordPolicyEntity;
    }

    private PasswordPolicy toDomain(PasswordPolicyEntity entity) {
        PasswordPolicy passwordPolicy = new PasswordPolicy();
        passwordPolicy.setId(entity.getId());
        passwordPolicy.setName(entity.getName());
        passwordPolicy.setValue(entity.getValue());
        passwordPolicy.setCaption(entity.getCaption());
        passwordPolicy.setDataType(entity.getDataType());
        passwordPolicy.setDescription(entity.getDescription());
        passwordPolicy.setValues(entity.getValues_());
        return passwordPolicy;
    }
}
