package com.jpmt.powerlab.security;

import com.jpmt.powerlab.services.UserService;
import jakarta.validation.ConstraintValidator;
import org.springframework.stereotype.Component;

@Component
public class ExisitByUsernameValidator implements ConstraintValidator<ExistByUsername, String> {

    private final UserService userService;

    public ExisitByUsernameValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String username, jakarta.validation.ConstraintValidatorContext context) {
        if (username == null || username.isEmpty()) {
            return true; // Let @NotBlank handle null or empty cases
        }
        return !userService.existByUsername(username);
    }
}
