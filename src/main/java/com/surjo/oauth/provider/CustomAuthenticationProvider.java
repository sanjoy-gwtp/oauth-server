package com.surjo.oauth.provider;

import com.surjo.oauth.model.User;
import com.surjo.oauth.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;


/**
 * Created by sanjoy on 5/14/17.
 */

public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    UserDetailsService userDetailsService;
    String userName;

//    @Autowired
//    CustomPasswordEncoder customPasswordEncoder;


    public CustomAuthenticationProvider() {

        userDetailsService=getUserDetailsService();



//        super.setSaltSource(userDetails -> (userDetails instanceof User) ? ((User) userDetails).getSalt() : null);
//
//        super.setPasswordEncoder(new PasswordEncoder() {
//            @Override
//            public String encodePassword(String rawPass, Object salt) {
//                return PasswordUtil.generateMD5(rawPass, (byte[]) salt);
//            }
//
//            @Override
//            public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
//                return encPass.equals(encodePassword(rawPass, salt));
//            }
//        });

        super.setPasswordEncoder(new PasswordEncoder() {

            @Override
            public String encode(CharSequence rawPassword) {
               // String hashed = BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt(12));
                return PasswordUtil.generateMD5(rawPassword.toString(),((User)userDetailsService.loadUserByUsername(userName)).getSalt());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                //return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
                //return getPasswordEncoder().matches(rawPassword,encodedPassword);
                return encodedPassword.equals(encode(rawPassword));
            }
        });

    }

//    @Override
//    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
//        if(authentication.getCredentials() == null) {
//            this.logger.debug("Authentication failed: no credentials provided");
//            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
//        } else {
//            String presentedPassword = authentication.getCredentials().toString();
//            if(!this.getPasswordEncoder().matches(userDetails.getPassword(),presentedPassword)) {
//                this.logger.debug("Authentication failed: password does not match stored value");
//                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
//            }
//        }
//    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        userName=authentication.getPrincipal().toString();
            User user = (User) super.retrieveUser(authentication.getPrincipal().toString(), new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    String.format("%s:%s", authentication.getPrincipal(), authentication.getCredentials()),
                    authentication.getAuthorities()
            ));
        Map<String,String> details=  (Map<String,String>)authentication.getDetails();

        if(!user.getClientId().equals(details.get("client_id"))){
            throw new InsufficientAuthenticationException("User Client Not Found.");
        }



        return super.authenticate(new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                String.format("%s:%s", authentication.getPrincipal(), authentication.getCredentials()),
                authentication.getAuthorities()) {

            @Override
            public void eraseCredentials() {
                super.eraseCredentials();
                ((UsernamePasswordAuthenticationToken) authentication).eraseCredentials();
            }

        });
    }
}
