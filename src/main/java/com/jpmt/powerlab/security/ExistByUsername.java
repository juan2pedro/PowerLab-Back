package com.jpmt.powerlab.security;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExisitByUsernameValidator.class)
public @interface ExistByUsername {
    String message() default "el nombre de usuario ya está en uso";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
