package com.surjo.oauth.service;

/**
 * Created by sanjoy on 5/17/17.
 */
public interface TotpService {
    String getTotp(String secret);
    Boolean verifyTotp(String secret,String token);
}
