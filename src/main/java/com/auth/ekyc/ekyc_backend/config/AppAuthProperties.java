package com.auth.ekyc.ekyc_backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.auth")
public class AppAuthProperties {
    private String emailVerifyPath;
    private int verifyTokenExpMinutes;
}
