package com.surjo.oauth.validator;



import com.surjo.oauth.annotation.ValidatePassword;
import com.surjo.oauth.model.PasswordPolicy;
import com.surjo.oauth.service.PasswordPolicyService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidatePassword, String> {

//    private String specialChars = "[!@#$%^&*()-_=+\\|[{]};:'\",<.>/?]";

    private final PasswordPolicyService passwordPolicyService;

    public PasswordValidator(PasswordPolicyService passwordPolicyService) {
        this.passwordPolicyService = passwordPolicyService;
    }

    @Override
    public void initialize(ValidatePassword constraintAnnotation) {

    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        return isValid(password).isValid();
    }

    public ValidatorResult isValid(String password) {
        List<PasswordPolicy> passwordPolicies = passwordPolicyService.findPasswordPolicies();

        for (PasswordPolicy policy: passwordPolicies){
            if(policy.getName().equals("PASSWORD_MINIMUM_LENGTH") && policy.getValue() != null && policy.getValue().isEmpty() == false){
                if(password.length() < Long.parseLong(policy.getValue())) {
                    return new ValidatorResult(
                            false, String.format("Passwords must be at least %s characters long",policy.getValue()));
                }
            }else if(policy.getName().equals("PASSWORD_MAXIMUM_LENGTH") && policy.getValue() != null && policy.getValue().isEmpty() == false){
                if(password.length() > Long.parseLong(policy.getValue())) {
                    return new ValidatorResult(
                            false, String.format("Password length must not exceed %s characters",policy.getValue()));
                }
            }else if(policy.getName().equals("HAVE_NUMBER") && policy.getValue() != null && policy.getValue().equals("true")){
                if(!password.matches("^(?=.*\\d).+$")) {
                    return new ValidatorResult(
                            false, "Password must have at least one numeric value");
                }
            }else if(policy.getName().equals("HAVE_CAPITAL_LETTER") && policy.getValue() != null && policy.getValue().equals("true")){
                if(!password.matches("^(?=.*[A-Z]).+$")) {
                    return new ValidatorResult(
                            false, "Password must have at least one upper case character");
                }
            }else if(policy.getName().equals("HAVE_SMALL_LETTER") && policy.getValue() != null && policy.getValue().equals("true")){
                if(!password.matches("^(?=.*[a-z]).+$")) {
                    return new ValidatorResult(
                            false, "Password must have at least one lower case character");
                }
            }else if(policy.getName().equals("HAVE_SPECIAL_CHARACTER") && policy.getValue() != null && policy.getValue().isEmpty() == false){
                System.out.println(policy.getValue());
                if(!Pattern.compile(policy.getValue()).matcher(password).find()) {
                    return new ValidatorResult(
                            false, "Password must have at least one special character");
                }
            }

        }
        return new ValidatorResult(true,"");
    }

    public static class ValidatorResult {
        private boolean valid;
        private String message;

        public ValidatorResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
