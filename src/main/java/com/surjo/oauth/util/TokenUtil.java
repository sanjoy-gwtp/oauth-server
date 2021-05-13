package com.surjo.oauth.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

import java.io.IOException;
import java.util.HashMap;

public class TokenUtil {
    public static String getJti(String token) {
        Jwt jwtToken = JwtHelper.decode(token);
        String claims = jwtToken.getClaims();
        HashMap claimsMap=new HashMap();
        try {
            claimsMap= new ObjectMapper().readValue(claims, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String jti=  claimsMap.get("jti").toString();
        return jti;
    }

    public static long getExpireTime(String token) {
        Jwt jwtToken = JwtHelper.decode(token);
        String claims = jwtToken.getClaims();
        HashMap claimsMap=new HashMap();

        try {
            claimsMap= new ObjectMapper().readValue(claims, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String exp=  claimsMap.get("exp").toString();
        return Long.valueOf(exp) * 1000;
    }
}
