package com.surjo.oauth.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * Created by sanjoy on 4/14/20.
 */
@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {

    int httpErrorCode;

    public CustomOauthException(String msg) {
        super(msg);
    }

    public CustomOauthException(String msg, Throwable exception) {
        super(msg, exception);

        if(msg.equalsIgnoreCase("Bad credentials")) {
            httpErrorCode=425;
        }
        else if(msg.equalsIgnoreCase("User credentials have expired")) {
            httpErrorCode=426;
        }
        else if(msg.equalsIgnoreCase("User account is locked")) {
            httpErrorCode=427;
        }
        else if(msg.equalsIgnoreCase("User account has expired")) {
            httpErrorCode=428;
        }
        else if(msg.equalsIgnoreCase("User is disabled")) {
            httpErrorCode=429;
        }
        else{
            httpErrorCode=400;
        }
    }

    @Override
    public int getHttpErrorCode() {
        return httpErrorCode;
    }
}
