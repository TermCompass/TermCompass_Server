package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.Request;
import lombok.Getter;

@Getter
public class RequestDTO {
    private Long id;
    private String request;
    private String answer;

    public RequestDTO(Request request) {
        this.id = request.getId();
        this.request = request.getRequest();
        this.answer = request.getAnswer();
    }
}
