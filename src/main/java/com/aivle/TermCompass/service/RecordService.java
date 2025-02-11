package com.aivle.TermCompass.service;

import com.aivle.TermCompass.domain.*;
import com.aivle.TermCompass.domain.Record;
import com.aivle.TermCompass.repository.DailyStatsRepository;
import com.aivle.TermCompass.repository.RecordRepository;
import com.aivle.TermCompass.repository.RequestRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final RequestRepository requestRepository;
    private final DailyStatsRepository dailyStatsRepository;

    
    @Value("${ngrok-host}")
    private String ngrokhost;

    public Record createRecord(User user, Record.RecordType recordType, String result, String title) {
        Record record = new Record();
        record.setUser(user);
        record.setRecord_type(recordType);
        record.setResult(result);
        record.setTitle(title);

        return recordRepository.save(record);
    }

    public void addRequest(Record record, String requestContent, FileEntity file, String answer) {
        Request request = new Request();
        request.setRecord(record);
        request.setRequest(requestContent);
        request.setFile(file);
        request.setAnswer(answer);
        request.setCreated_at(LocalDateTime.now());

        requestRepository.save(request);
    }

    @Transactional
    public List<Record> getRecordsByUser(User user) {
        return recordRepository.findAllByUser(user);
    }

    @Transactional
    public List<Record> getRecordsByUserWithoutResult(User user) {
        return recordRepository.findAllByUserWithoutResult(user);
    }
    
    public String getChatbotResponse(Long userId, String request) {
        String url = "https://"+ngrokhost+"/chatbot";

        // 🔹 요청 데이터 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("question", request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("response").asText(); // 🔹 응답 데이터 반환
        } catch (Exception e) {
            e.printStackTrace();
            return "Chatbot API Error"; // 🔹 오류 발생 시 기본 응답
        }
    }

    @Transactional
    public void incrementGenerateCount() {
        LocalDate today = LocalDate.now();
        DailyStats stats = dailyStatsRepository.findByDate(today)
                .orElseGet(() -> dailyStatsRepository.save(new DailyStats(null, today, 0, 0, 0, 0)));

        stats.incrementGenerateCount();
        dailyStatsRepository.save(stats);
    }

    @Transactional
    public void incrementReviewCount() {
        LocalDate today = LocalDate.now();
        DailyStats stats = dailyStatsRepository.findByDate(today)
                .orElseGet(() -> dailyStatsRepository.save(new DailyStats(null, today, 0, 0, 0, 0)));

        stats.incrementReviewCount();
        dailyStatsRepository.save(stats);
    }

    @Transactional
    public void incrementChatCount() {
        LocalDate today = LocalDate.now();
        DailyStats stats = dailyStatsRepository.findByDate(today)
                .orElseGet(() -> dailyStatsRepository.save(new DailyStats(null, today, 0, 0, 0, 0)));

        stats.incrementChatCount();
        dailyStatsRepository.save(stats);
    }
}
