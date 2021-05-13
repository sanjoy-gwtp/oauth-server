package com.surjo.oauth.model;

/**
 * Created by sanjoy on 2/14/19.
 */
public class UserAttemptDetail {

    private long id;
    private String userName;
    private String  ipAddress;
    private long creationTime;
    private String status;

    public UserAttemptDetail(long id, String userName, String ipAddress, long creationTime, String status) {
        this.id = id;
        this.userName = userName;
        this.ipAddress = ipAddress;
        this.creationTime = creationTime;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
