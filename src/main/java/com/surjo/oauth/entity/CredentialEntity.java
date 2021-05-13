package com.surjo.oauth.entity;

import com.surjo.oauth.model.CredentialAlgorithm;
import com.surjo.oauth.model.CredentialType;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "CREDENTIAL")
public class CredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CREDENTIAL_TYPE")
    @Enumerated(EnumType.STRING)
    private CredentialType type;

    @Enumerated(EnumType.STRING)
    private CredentialAlgorithm algorithm;

    private String value;

    @Lob
    private byte[] salt;

    private LocalDate expireDate;
    private LocalTime expireTime;

    private String device;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CredentialType getType() {
        return type;
    }

    public void setType(CredentialType type) {
        this.type = type;
    }

    public CredentialAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(CredentialAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public LocalTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalTime expireTime) {
        this.expireTime = expireTime;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}

