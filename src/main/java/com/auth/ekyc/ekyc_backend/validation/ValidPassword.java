package com.auth.ekyc.ekyc_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface ValidPassword {

    String message() default "Password must be at least 6 characters and include uppercase, lowercase and special character";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
