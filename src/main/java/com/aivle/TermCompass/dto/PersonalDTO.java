package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PersonalDTO {
    private Long id;
    private String email;
    private String name;
    private User.AccountType account_type;
    private LocalDateTime created_at;

    public PersonalDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.account_type = user.getAccount_type();
        this.created_at = user.getCreated_at();
    }
}
