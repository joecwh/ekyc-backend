package com.auth.ekyc.ekyc_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EkycBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EkycBackendApplication.class, args);
	}
}
