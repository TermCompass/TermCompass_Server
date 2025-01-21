package com.aivle.TermCompass.service;

import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(String name, String email, String password, User.AccountType account_type) {
        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAccount_type(account_type);
        user.setCreated_at(LocalDateTime.now());
        this.userRepository.save(user);

        return user;
    }

    public User create(String name, String email, String password, User.AccountType account_type, String businessNumber) {
        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAccount_type(account_type);
        user.setBusinessNumber(businessNumber);
        user.setCreated_at(LocalDateTime.now());
        this.userRepository.save(user);

        return user;
    }

    public boolean authenticate(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return false; // 유저를 찾지 못한 경우
        }

        User user = optionalUser.get();
        return passwordEncoder.matches(password, user.getPassword());
    }
}