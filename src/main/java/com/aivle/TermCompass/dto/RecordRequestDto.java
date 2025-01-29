package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.Record;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RecordRequestDto {
    private Long userId;
    private Record.RecordType recordType;
    private String result;
    private String request;
    private String file;
    private String answer;
}
