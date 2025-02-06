package com.aivle.TermCompass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@EnableJpaRepositories(basePackages = "com.aivle.TermCompass.repository")
public class TermCompassApplication {

	public static void main(String[] args) {
		SpringApplication.run(TermCompassApplication.class, args);
	}

}
