package com.surjo.oauth.service.impl;

import com.surjo.oauth.model.UserAttemptDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.Date;
import java.util.List;

/**
 * Created by sanjoy on 2/14/19.
 */
@Service
public class LoginAttemptService {


    @Value("${mislbd.auth.max-attempt:5}")
    private long maxAttempt;

    @Value("${mislbd.auth.last-attempt-fail-in-minute:30}")
    private long lastAttemptTimeInMinute;

    private static final String SQL_CREATE_USER_SESSION =
            "INSERT INTO user_login_attempt(USER_NAME, IP_ADDRESS,CREATION_TIME,STATUS) VALUES (?, ?, ?,?)";

    private static final String SQL_GET_ATTEMPT_USER="SELECT * FROM user_login_attempt S " +
            "WHERE S.USER_NAME=? AND S.CREATION_TIME BETWEEN ?  AND ? ORDER BY S.ID DESC";


    private static final String SQL_UPDATE_USER_FOR_LOCK=
            "UPDATE user S SET S.ACC_NOT_LOCKED=? WHERE S.USER_NAME=?";

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Transactional
    public void loginFailed(String userName,String ipAddress) {
        System.out.println(userName+" ,  "+ipAddress);
        long now = new Date().getTime();
        List<UserAttemptDetail> userAttemptDetails= loadUserByUsername(userName,now-(lastAttemptTimeInMinute*60*1000),now);

        int failCount=0;
        for (UserAttemptDetail userAttemptDetail :userAttemptDetails) {
            if(userAttemptDetail.getStatus().equals("FAILED")){
                failCount=failCount+1;
            }else{
                break;
            }
        }
        if(failCount>=maxAttempt-1){
            jdbcTemplate.update( SQL_UPDATE_USER_FOR_LOCK,
                    (new Object[]{false, userName}),
                    (new int[]{Types.BOOLEAN,Types.VARCHAR}));
        }
        jdbcTemplate.update( SQL_CREATE_USER_SESSION,
                (new Object[]{userName, ipAddress,new Date().getTime(),"FAILED"}),
                (new int[]{Types.VARCHAR, Types.VARCHAR,Types.NUMERIC,Types.VARCHAR}));
    }

    @Transactional
    public void loginSucceeded(String userName,String ipAddress) {
        System.out.println(userName+" ,  "+ipAddress);
        jdbcTemplate.update( SQL_CREATE_USER_SESSION,
                (new Object[]{userName, ipAddress,new Date().getTime(),"SUCCEEDED"}),
                (new int[]{Types.VARCHAR, Types.VARCHAR,Types.NUMERIC,Types.VARCHAR}));
    }


    public List<UserAttemptDetail> loadUserByUsername(String username,long from,long to) {
        return jdbcTemplate.query(SQL_GET_ATTEMPT_USER, new Object[]{username,from,to},
                (RowMapper<UserAttemptDetail>) (rs, rn) -> new UserAttemptDetail(
                        rs.getLong("ID"),
                        rs.getString("USER_NAME"),
                        rs.getString("IP_ADDRESS"),
                        rs.getLong("CREATION_TIME"),
                        rs.getString("STATUS")
                ));
    }
}
