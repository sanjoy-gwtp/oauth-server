package com.surjo.oauth.notification;

/**
 * Created by sanjoy on 5/17/17.
 */
public interface SmsNotificationService {
     boolean sendSms(String mobileNo,String msg) throws Exception;
}
