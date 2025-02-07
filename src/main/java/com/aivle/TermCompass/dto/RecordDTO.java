package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.Record;
import com.aivle.TermCompass.domain.Request;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RecordDTO {
    private Long id;
    private String result;
    private Record.RecordType recordType;
    private List<RequestDTO> requests;

    public RecordDTO(Record record) {
        this.id = record.getId();
        this.result = record.getResult();
        this.recordType = record.getRecord_type();
        this.requests = record.getRequests().stream()
                            .map(RequestDTO::new)
                            .collect(Collectors.toList());
    }
}
