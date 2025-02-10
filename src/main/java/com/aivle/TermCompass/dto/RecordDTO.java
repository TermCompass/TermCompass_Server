package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.Record;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
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
