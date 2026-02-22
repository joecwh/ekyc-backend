package com.auth.ekyc.ekyc_backend.validation;

import com.auth.ekyc.ekyc_backend.application.dto.auth.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request.getPassword() == null || request.getConfirmPassword() == null)
            return false;

        boolean matched = request.getPassword().equals(request.getConfirmPassword());

        if (!matched) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password and confirm password must match")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return matched;
    }
}
