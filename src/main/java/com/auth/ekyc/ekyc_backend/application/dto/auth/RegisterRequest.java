package com.auth.ekyc.ekyc_backend.application.dto.auth;

import com.auth.ekyc.ekyc_backend.validation.PasswordMatches;
import com.auth.ekyc.ekyc_backend.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatches
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @ValidPassword
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "Invalid phone number")
    private String phoneNumber;
}
