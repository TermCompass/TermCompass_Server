package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.Question;
import com.aivle.TermCompass.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class QuestionDetailDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime created_at;
    private List<AnswerDTO> answers;

    // 생성자
    public QuestionDetailDTO(Question question, User user) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.author = user.getName();
        this.created_at = question.getCreated_at();
        this.answers = question.getAnswerList().stream()
                .map(AnswerDTO::new)
                .collect(Collectors.toList());
    }
}

