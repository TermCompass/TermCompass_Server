package com.aivle.TermCompass.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private LocalDate date;
    private int loginCount = 0;
    private int GenerateCount = 0;
    private int ReviewCount = 0;
    private int ChatCount = 0;

    public void incrementLoginCount() {
        this.loginCount++;
    }

    public void incrementGenerateCount() {
        this.GenerateCount++;
    }

    public void incrementReviewCount() {
        this.ReviewCount++;
    }

    public void incrementChatCount() {
        this.ChatCount++;
    }
}
