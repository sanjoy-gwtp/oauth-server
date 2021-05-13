package com.surjo.oauth.service.impl;

import com.surjo.oauth.service.TotpService;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Clock;
import org.springframework.stereotype.Service;

/**
 * Created by sanjoy on 5/17/17.
 */
@Service
public class TotpServiceImpl implements TotpService {

    @Override
    public String getTotp(String secret) {
        Totp totp=new Totp(secret,new Clock(60));
        return totp.now();
    }

    @Override
    public Boolean verifyTotp(String secret, String token) {
        Totp totp=new Totp(secret,new Clock(60));
        return totp.verify(token);
    }
}
