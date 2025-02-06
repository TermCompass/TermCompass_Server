package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.Answer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnswerDTO {
    private Long id;
    private String content;
    private String author;
    private LocalDateTime created_at;

    public AnswerDTO(Answer answer) {
        this.id = answer.getId();
        this.content = answer.getContent();
        this.author = answer.getUser().getName(); // 댓글 작성자 이름
        this.created_at = answer.getCreated_at();
    }
}
