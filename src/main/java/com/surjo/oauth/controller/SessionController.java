package com.surjo.oauth.controller;

import com.surjo.oauth.session.UserSessionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class SessionController {

    private final UserSessionService userSessionService;

    public SessionController(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @RequestMapping(method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> destroySession(@RequestParam(name = "users", required = true) List<Map<String,String>> userList) {
        userSessionService.destroyUserSession(userList);
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> sessions(
            @RequestParam(name = "client_id", required = false) String clientId,
            @RequestParam(name = "username", required = false) String username) {
        return  ResponseEntity.ok(userSessionService.getSessions(clientId, username));
    }
}
