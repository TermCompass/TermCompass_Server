package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CompanyDTO {
    private String email;
    private String name;
    private User.AccountType account_type;
    private LocalDateTime created_at;
    private String businessNumber;

    public CompanyDTO(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.account_type = user.getAccount_type();
        this.created_at = user.getCreated_at();
        this.businessNumber = user.getBusinessNumber();
    }
}
