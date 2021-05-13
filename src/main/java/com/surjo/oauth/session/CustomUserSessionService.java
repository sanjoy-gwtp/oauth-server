//package com.surjo.oauth.session;
//
//
//import CustomTokenServices;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.core.session.SessionInformation;
//import org.springframework.security.core.session.SessionRegistry;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.sql.Timestamp;
//import java.sql.Types;
//import java.util.List;
//
//@Service
//public class CustomUserSessionService {
//
//    private static final String SQL_CREATE_USER_SESSION =
//            "INSERT INTO USER_SESSION(USERNAME, SESSION_ID, TERMINAL, USER_BRANCH, SESSION_STATUS, CREATION_TIME)" +
//            " VALUES (?, ?, ?, ?, 'ACTIVE', ?)";
//    private static final String SQL_DESTROY_USER_SESSION_BY_OWN =
//            "UPDATE USER_SESSION SET SESSION_STATUS = 'DESTROYED_BY_OWN' WHERE USERNAME = ? AND SESSION_STATUS='ACTIVE'";
//    private static final String SQL_DESTROY_USER_SESSION_BY_ADMIN =
//            "UPDATE USER_SESSION SET SESSION_STATUS = 'DESTROYED_BY_ADMIN' WHERE USERNAME = ? AND SESSION_STATUS='ACTIVE'";
//    private static final String SQL_DESTROY_USER_SESSION_BY_SYSTEM =
//            "UPDATE USER_SESSION SET SESSION_STATUS = 'DESTROYED_BY_SYSTEM' WHERE USERNAME = ? AND SESSION_STATUS='ACTIVE'";
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//
////    @Autowired
////    private CustomTokenServices tokenServices;
//
////    @Autowired
////    CustomSessionRepository customSessionRepository;
//
//    @Transactional
//    public void createUserSession(String username, String sessionId, String terminal, long userBranch,  long time){
//
//        jdbcTemplate.update( SQL_CREATE_USER_SESSION,
//                (new Object[]{username, sessionId, terminal, userBranch, new Timestamp(time)}),
//                (new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT, Types.TIMESTAMP})
//        );
//    }
//
//
//    @Transactional
//    public void destroySessionByOwn(List<String> users){
//        users.forEach(user -> {
//            //customSessionRepository.deleteSessionByUserName(user);
//            //tokenServices.revokeTokenByUserName(user);
//            jdbcTemplate.update( SQL_DESTROY_USER_SESSION_BY_OWN,
//                    new Object[]{user}, new int[]{Types.VARCHAR} );
//        });
//    }
//
//    @Transactional
//    public void destroySessionByAdmin(List<String> users){
//        users.forEach(user -> {
//            //customSessionRepository.deleteSessionByUserName(user);
//            //tokenServices.revokeTokenByUserName(user);
//            jdbcTemplate.update( SQL_DESTROY_USER_SESSION_BY_ADMIN,
//                    new Object[]{user}, new int[]{Types.VARCHAR} );
//        });
//    }
//
//    @Transactional
//    public void destroySessionBySystem(List<String> users){
//        users.forEach(user -> {
//            //tokenServices.revokeTokenByUserName(user);
//            jdbcTemplate.update( SQL_DESTROY_USER_SESSION_BY_SYSTEM,
//                    new Object[]{user}, new int[]{Types.VARCHAR} );
//        });
//    }
//
//}
