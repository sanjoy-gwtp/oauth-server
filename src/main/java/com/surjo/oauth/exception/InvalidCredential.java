package com.surjo.oauth.exception;

public class InvalidCredential extends RuntimeException {
    //super("0010", "Invalid Credential");
    public InvalidCredential() {
    }

    public InvalidCredential(String s) {
        super(s);
    }

    public InvalidCredential(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidCredential(Throwable throwable) {
        super(throwable);
    }
}
