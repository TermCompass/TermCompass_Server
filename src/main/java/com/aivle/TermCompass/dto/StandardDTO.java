package com.aivle.TermCompass.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StandardDTO {
    private Long id;
    private String filename;
    private String refined_text;

    public StandardDTO(Long id, String filename) {
        this.id = id;
        this.filename = filename;
    }
}
