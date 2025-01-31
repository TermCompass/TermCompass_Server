package com.aivle.TermCompass.config;

import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class AdminInitializer {
    @Bean
    public CommandLineRunner initData(UserRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if(repository.findByEmail("admin@termcompass.com").isEmpty()) {
                User admin = new User();
                admin.setName("admin");
                admin.setEmail("admin@termcompass.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setCreated_at(LocalDateTime.now());
                admin.setAccount_type(User.AccountType.ADMIN);
                repository.save(admin);
            }
        };
    }
}
