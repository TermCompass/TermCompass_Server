package com.aivle.TermCompass.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountType account_type;

    @Column
    private String name;

    @Column
    private String businessNumber;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private LocalDateTime created_at;

    public enum AccountType {
        PERSONAL, COMPANY, ADMIN
    }
}
