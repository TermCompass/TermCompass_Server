package com.aivle.TermCompass.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TermList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(columnDefinition = "TEXT") // 긴 텍스트 저장
    private String content;

    @Enumerated(EnumType.STRING)
    private Evaluation evaluation;

    private String title;

    @Column(columnDefinition = "TEXT") // 긴 텍스트 저장
    private String summary;

    public enum Evaluation {
        ADVANTAGE, DISADVANTAGE
    }

    public TermList() {
    }
}
