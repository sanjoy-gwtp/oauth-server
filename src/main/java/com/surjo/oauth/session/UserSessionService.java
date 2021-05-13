package com.surjo.oauth.session;

import com.surjo.oauth.provider.CustomClientDetailsProvider;
import com.surjo.oauth.service.impl.CustomRedisTokenStore;
import com.surjo.oauth.service.impl.CustomTokenServices;
import com.surjo.oauth.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


@Service
public class UserSessionService {

    private static final String SQL_CREATE_USER_SESSION =
            "INSERT INTO user_session( USERNAME, USER_FULL_NAME, SESSION_ID, CLIENT_ID, TERMINAL, USER_BRANCH, SESSION_STATUS, REFRESH_TOKEN, CREATION_TIME, EXPIRE_TIME)" +
            " VALUES ( ?, ?, ?, ?, ?, ?, 'ACTIVE',?, ?,?)";
    private static final String SQL_DESTROY_USER_SESSION_BY_OWN = "UPDATE user_session SET SESSION_STATUS = 'DESTROYED_BY_OWN' WHERE SESSION_STATUS='ACTIVE' AND USERNAME = ?";
    private static final String SQL_DESTROY_USER_SESSION_BY_ADMIN = "UPDATE user_session SET SESSION_STATUS = 'DESTROYED_BY_ADMIN' WHERE SESSION_STATUS='ACTIVE' AND USERNAME = ?";
    private static final String SQL_DESTROY_USER_SESSION_BY_SYSTEM = "UPDATE user_session SET SESSION_STATUS = 'DESTROYED_BY_SYSTEM' WHERE SESSION_STATUS='ACTIVE' AND SESSION_ID = ?";
    private static final String SQL_DESTROY_SESSION_BY_USER = "UPDATE user_session SET SESSION_STATUS = 'DESTROYED_BY_OWN' WHERE SESSION_STATUS='ACTIVE' AND SESSION_ID = ?";
    private static final String SQL_DESTROY_EXPIRED_SESSION = "UPDATE user_session SET SESSION_STATUS = 'DESTROYED_BY_SYSTEM' WHERE SESSION_STATUS='ACTIVE' AND EXPIRE_TIME <= ?";
    private static final String SQL_DESTROY_SESSION_BY_ADMIN = "UPDATE USER_SESSION SET SESSION_STATUS = 'DESTROYED_BY_ADMIN' WHERE SESSION_STATUS='ACTIVE' AND SESSION_ID = ?";
    private static final String SQL_SESSION_BY_ID = "SELECT USERNAME, SESSION_ID, REFRESH_TOKEN, EXPIRE_TIME FROM user_session WHERE SESSION_ID = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CustomRedisTokenStore tokenStore;

    @Autowired
    private CustomTokenServices tokenServices;

    @Autowired
    private CustomClientDetailsProvider clientDetailsService;


    @Transactional
    public void createUserSession(String username, String userFullName, String sessionId, String clientId, String terminal, long userBranch, String refreshToken,  long time, long expire){

        jdbcTemplate.update( SQL_CREATE_USER_SESSION,
                (new Object[]{username, userFullName, sessionId, clientId, terminal, userBranch, refreshToken, new Timestamp(time), new Timestamp(expire)}),
                (new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT, Types.CLOB, Types.TIMESTAMP, Types.TIMESTAMP})
        );
    }

    @Transactional
    public void destroyUserSession(List<Map<String, String>> sessions){
        sessions.forEach(session -> {
            sessionInvalid(session);
            jdbcTemplate.update( SQL_DESTROY_SESSION_BY_ADMIN,
                    new Object[]{session.get("sessionId")}, new int[]{Types.VARCHAR} );
        });
        dropExpiredSessions();
    }

    @Transactional
    public void sessionInvalidBySystem(String jti){
            jdbcTemplate.update( SQL_DESTROY_USER_SESSION_BY_SYSTEM,
                    new Object[]{jti}, new int[]{Types.VARCHAR} );
        dropExpiredSessions();
    }

    private void dropExpiredSessions() {
        jdbcTemplate.update(SQL_DESTROY_EXPIRED_SESSION,
                new Object[]{Timestamp.valueOf(LocalDateTime.now())}, new int[]{Types.TIMESTAMP});
    }

    private void sessionInvalid(Map<String, String> session){
        tokenStore.revokeTokenByUserName(session.get("username"), session.get("sessionId"), session.get("clientId"));
    }


    @Transactional
    public void destroySession(String expiredSessionId) {
        jdbcTemplate.update( SQL_DESTROY_SESSION_BY_USER,
                new Object[]{expiredSessionId}, new int[]{Types.VARCHAR} );

        dropExpiredSessions();
    }

    public Object getSessions(String clientId, String username) {

        List<OAuth2AccessToken> tokens = tokenServices.getAccessTokens(clientId, username);
        final List<Map<String,Object>> sessions = new ArrayList<>();

        tokens.forEach(accessToken -> {
            OAuth2Authentication authentication = tokenServices.getAuthentication(accessToken);
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(authentication.getOAuth2Request().getClientId());

            Map<String,Object> session = new HashMap<>();

            Calendar createTime = Calendar.getInstance();
            createTime.setTime(accessToken.getExpiration());
            createTime.add(Calendar.SECOND, (clientDetails.getAccessTokenValiditySeconds() * -1));

            session.put("sessionId", TokenUtil.getJti(accessToken.getRefreshToken().getValue()));
            session.put("username", authentication.getName());
            session.put("clientId", clientDetails.getClientId());
            session.put("terminal",null);
            session.put("creationTime", LocalDateTime.ofInstant(createTime.toInstant(), ZoneId.systemDefault()));
            session.put("userBranch",accessToken.getAdditionalInformation().get("activeBranch"));
            session.put("sessionStatus", accessToken.isExpired() ? "DESTROYED"  : "ACTIVE");
            sessions.add(session);
        });

        return sessions;
    }

    public Map<String,Object> getSession(String sessionId) {
        return jdbcTemplate.query(SQL_SESSION_BY_ID, new Object[]{sessionId}, new ResultSetExtractor<Map<String,Object>>() {
            @Override
            public Map<String, Object> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if(resultSet.next()) {
                    Map<String,Object> result = new HashMap<>();
                    result.put("USERNAME", resultSet.getString("USERNAME"));
                    result.put("SESSION_ID", resultSet.getString("SESSION_ID"));
                    result.put("REFRESH_TOKEN", resultSet.getString("REFRESH_TOKEN"));
                    result.put("EXPIRE_TIME", resultSet.getTimestamp("EXPIRE_TIME"));
                    return result;
                }
                return null;
            }
        });
    }
}
