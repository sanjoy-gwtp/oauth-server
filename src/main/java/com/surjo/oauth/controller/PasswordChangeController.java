package com.surjo.oauth.controller;



import com.surjo.oauth.model.ChangeCredential;
import com.surjo.oauth.model.Credential;
import com.surjo.oauth.model.CredentialType;
import com.surjo.oauth.service.PasswordPolicyService;
import com.surjo.oauth.service.impl.CredentialService;
import com.surjo.oauth.validator.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("password")
public class PasswordChangeController {

    Logger logger = LoggerFactory.getLogger(PasswordChangeController.class);

    private final CredentialService credentialService;
    private final PasswordValidator passwordValidator;

    public PasswordChangeController(CredentialService credentialService, PasswordPolicyService passwordPolicyService) {
        this.credentialService = credentialService;
        this.passwordValidator = new PasswordValidator(passwordPolicyService);
    }

    @RequestMapping(path = "change",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Boolean changePassword(@RequestBody ChangeCredential changeCredential) {
        changeCredential.setType(CredentialType.PASSWORD);
        PasswordValidator.ValidatorResult validatorMessage = passwordValidator.isValid(changeCredential.getNewValue());
        return credentialService.changeCredential(changeCredential);
    }

    @RequestMapping(path = "forgot",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean forgotPassword(@RequestBody Credential credential) {
        credential.setType(CredentialType.PASSWORD);
        return credentialService.forgotCredential(credential);
    }
}
