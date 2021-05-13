package com.surjo.oauth.controller;

import com.surjo.oauth.service.TotpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by sanjoy on 5/17/17.
 */
@RestController
public class TotpController {

    @Autowired
    TotpService totpService;

    @RequestMapping("/2fa/pass")
    public String getOneTimePass(@RequestParam String secret) {
        return totpService.getTotp(secret);
    }

    @RequestMapping("/2fa/verify")
    public boolean verifyOneTimePass(@RequestParam String secret,@RequestParam String token) {
        return totpService.verifyTotp(secret,token);
    }

}
