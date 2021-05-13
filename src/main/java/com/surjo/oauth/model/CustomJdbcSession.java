package com.surjo.oauth.model;

/**
 * Created by sanjoy on 2/11/19.
 */
public class CustomJdbcSession {

    private String sessionId;
    private String userName;

    public CustomJdbcSession(String sessionId, String userName) {
        this.sessionId = sessionId;
        this.userName = userName;
    }

    public CustomJdbcSession() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
