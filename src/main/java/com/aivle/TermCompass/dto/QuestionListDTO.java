package com.aivle.TermCompass.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuestionListDTO {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime created_at;

    public QuestionListDTO(Long id, String title, String author, LocalDateTime created_at) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.created_at = created_at;
    }
}
