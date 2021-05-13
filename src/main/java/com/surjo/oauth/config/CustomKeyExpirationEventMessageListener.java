package com.surjo.oauth.config;


import com.surjo.oauth.session.UserSessionService;
import com.surjo.oauth.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

public class CustomKeyExpirationEventMessageListener extends KeyExpirationEventMessageListener {

    @Autowired
    UserSessionService sessionService;

    public CustomKeyExpirationEventMessageListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        super.setApplicationEventPublisher(applicationEventPublisher);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

       String[] token= message.toString().split(":");
       if(token[0].startsWith("refresh")){
           String jti = TokenUtil.getJti(token[1]);
           sessionService.sessionInvalidBySystem(jti);
       }
        super.onMessage(message, pattern);
    }

}
