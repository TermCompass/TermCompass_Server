package com.aivle.TermCompass.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "record_id")
    private Record record;

    @Column
    private String request;

    @Column
    private String file;

    @Column
    private String answer;

    @Column
    private LocalDateTime created_at;
}
