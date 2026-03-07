package com.foodsystem.user_identity_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class UserIdentityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserIdentityServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
    	return new RestTemplate();
	}

}
