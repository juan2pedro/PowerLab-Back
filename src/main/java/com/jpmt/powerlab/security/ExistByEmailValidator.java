package com.jpmt.powerlab.security;


import com.jpmt.powerlab.services.UserService;
import jakarta.validation.ConstraintValidator;
import org.springframework.stereotype.Component;

@Component
public class ExistByEmailValidator implements ConstraintValidator<ExistByEmail, String> {

    private final UserService userService;

    public ExistByEmailValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String email, jakarta.validation.ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true; // Let @NotBlank handle null or empty cases
        }
        return !userService.existByEmail(email);
    }

}
