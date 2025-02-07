package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.FileEntity;
import com.aivle.TermCompass.domain.Record;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RecordRequestDto {
    private Long userId;
    private Record.RecordType recordType;
    private Long recordId;
    private String result;
    private String request;
    private FileEntity file;
    private String answer;
}
