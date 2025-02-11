package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.TermList;
import lombok.Getter;

@Getter
public class TermListDTO {
    private Long id;
    private String content;
    private TermList.Evaluation evaluation;
    private String title;
    private String summary;

    public TermListDTO(TermList termList) {
        this.id = termList.getId();
        this.content = termList.getContent();
        this.evaluation = termList.getEvaluation();
        this.title = termList.getTitle();
        this.summary = termList.getSummary();
    }
}
