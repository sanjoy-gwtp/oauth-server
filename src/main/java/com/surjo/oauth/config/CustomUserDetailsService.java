package com.surjo.oauth.config;



import com.surjo.oauth.model.Right;
import com.surjo.oauth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static final String GET_USER_BY_USERNAME="SELECT U.ID, USER_NAME AS USERNAME, C.value AS PASSWORD,\n" +
            "C.salt AS SALT,FIRST_NAME,LAST_NAME,ENABLED,EXPIRE_DATE,CLIENT_ID," +
            "ACC_NOT_EXPIRED,ACC_NOT_LOCKED,CREDENTIAL_NOT_EXP,MENU_GROUP,BRANCH FROM user U,credential C WHERE\n" +
            "  U.ID=C.USER_ID AND\n" +
            "  U.USER_NAME=?";

    private static final String GET_RIGHT_BY_USERNAME="SELECT R.NAME FROM rights R,role_rights RR,user_roles UR,user U "
                            +"WHERE U.ID=UR.USERS_ID AND UR.ROLES_ID=RR.ROLES_ID AND RR.RIGHTS_ID=R.ID AND U.USER_NAME=?";



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userDetail=null;
        try {
            userDetail = jdbcTemplate.queryForObject(GET_USER_BY_USERNAME, new Object[]{username}, new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet resultSet, int i) throws SQLException {
                    User user = new User();

                    Date now = new Date();
                    Date expireDate = resultSet.getDate("EXPIRE_DATE");
                    if (expireDate == null) {
                        expireDate = new Date(now.getTime());
                    }
                    user.setId(resultSet.getLong("ID"));
                    user.setUserName(resultSet.getString("USERNAME"));
                    user.setPassword(resultSet.getString("PASSWORD"));
                    user.setSalt(resultSet.getBytes("SALT"));
                    user.setFirstName(resultSet.getString("FIRST_NAME"));
                    user.setLastName(resultSet.getString("LAST_NAME"));
                    user.setEnabled(resultSet.getBoolean("ENABLED"));
                    user.setAccountNonExpired(resultSet.getBoolean("ACC_NOT_EXPIRED"));
                    user.setAccountNonLocked(resultSet.getBoolean("ACC_NOT_LOCKED"));
                    user.setCredentialsNonExpired(!(now.getTime() >= expireDate.getTime()));
                    user.setMenuGroup(resultSet.getLong("MENU_GROUP"));
                    user.setBranch(resultSet.getLong("BRANCH"));
                    user.setRights((List<Right>) getUserGrantedAuthorities(username));
                    user.setUserFullName("");
                    user.setClientId(resultSet.getString("CLIENT_ID"));
                    return user;
                }
            });
        }catch (Exception ex){
           throw new UsernameNotFoundException("User Name Not Found!");
        }

        if (userDetail.getUserName() == null) {
            throw new UsernameNotFoundException(String.format("User %s does not exist!", username));
        }
        return userDetail;
    }

    private Collection<? extends GrantedAuthority> getUserGrantedAuthorities(String username) {
        return jdbcTemplate.query(
                GET_RIGHT_BY_USERNAME,
                new Object[]{username},
                (RowMapper<GrantedAuthority>) (rs, rn) -> new Right(rs.getString(1)));
    }

}
