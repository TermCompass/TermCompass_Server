package com.aivle.TermCompass.service;

import com.aivle.TermCompass.domain.Record;
import com.aivle.TermCompass.domain.Request;
import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.repository.RecordRepository;
import com.aivle.TermCompass.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final RequestRepository requestRepository;

    public Record createRecord(User user, Record.RecordType recordType, String result) {
        Record record = new Record();
        record.setUser(user);
        record.setRecord_type(recordType);
        record.setResult(result);

        return recordRepository.save(record);
    }

    public void addRequest(Record record, String requestContent, String file, String answer) {
        Request request = new Request();
        request.setRecord(record);
        request.setRequest(requestContent);
        request.setFile(file);
        request.setAnswer(answer);
        request.setCreated_at(LocalDateTime.now());

        requestRepository.save(request);
    }

    public List<Record> getRecordsByUser(User user) {
        return recordRepository.findAllByUser(user);
    }
}
