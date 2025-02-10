package com.aivle.TermCompass.controller;

import com.aivle.TermCompass.domain.Record;
import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.domain.Record.RecordType;
import com.aivle.TermCompass.dto.RecordDTO;
import com.aivle.TermCompass.dto.RecordRequestDto;
import com.aivle.TermCompass.repository.RecordRepository;
import com.aivle.TermCompass.repository.UserRepository;
import com.aivle.TermCompass.service.JwtTokenProvider;
import com.aivle.TermCompass.service.PostService;
import com.aivle.TermCompass.service.RecordService;
import com.aivle.TermCompass.service.RequestService;
import com.aivle.TermCompass.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
@Controller
public class RecordController {
    private final RecordService recordService;
    private final PostService postService;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/create-terms")
    public Mono<ResponseEntity<Map<String, Object>>> createTermsRecord(@RequestBody RecordRequestDto recordRequestDto) {
        Optional<User> optionalUser = userRepository.findById(recordRequestDto.getUserId());
        if (optionalUser.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "User not found.")));
        }

        User user = optionalUser.get();
        Record record = recordService.createRecord(user, recordRequestDto.getRecordType(), null);

        Map<String, Object> requestData = Map.of("text", recordRequestDto.getRequest());

        return postService.sendPostRequest("/generate", requestData)
                .map(response -> {
                    record.setResult(response);
                    recordService.addRequest(record, recordRequestDto.getRequest(), recordRequestDto.getFile(),
                            response);

                    return ResponseEntity.ok(Map.of(
                            "answer", response));
                });
    }

    @PostMapping("/review-terms")
    public Mono<ResponseEntity<Map<String, Object>>> reviewTermsRecord(@RequestBody RecordRequestDto recordRequestDto) {
        Optional<User> optionalUser = userRepository.findById(recordRequestDto.getUserId());
        if (optionalUser.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "User not found.")));
        }

        User user = optionalUser.get();
        Record record = recordService.createRecord(user, recordRequestDto.getRecordType(), null);

        Map<String, Object> requestData = Map.of("text", recordRequestDto.getRequest());

        return postService.sendPostRequest("/review", requestData)
                .map(response -> {
                    record.setResult(response);
                    recordService.addRequest(record, recordRequestDto.getRequest(), recordRequestDto.getFile(),
                            response);

                    return ResponseEntity.ok(Map.of(
                            "answer", response));
                });
    }

    @PostMapping("/create-chat")
    public ResponseEntity<Object> createChatRecord(@RequestBody RecordRequestDto recordRequestDto) {
        Long userId = recordRequestDto.getUserId();
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        User user = optionalUser.get();
        Record record;

        if (recordRequestDto.getRecordId() != null) {
            Optional<Record> optionalRecord = recordRepository.findById(recordRequestDto.getRecordId());
            if (optionalRecord.isPresent()) {
                record = optionalRecord.get();
            } else {
                return ResponseEntity.badRequest().body("Invalid Record ID.");
            }
        } else {
            record = recordService.createRecord(user, Record.RecordType.CHAT, recordRequestDto.getRequest());
            recordService.incrementChatCount();
        }
        String chatbotResponse = recordService.getChatbotResponse(recordRequestDto.getUserId(),
                recordRequestDto.getRequest());

        recordService.addRequest(record, recordRequestDto.getRequest(), null, chatbotResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("recordId", record.getId());
        response.put("response", chatbotResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/records/{userId}")
    public ResponseEntity<List<RecordDTO>> getRecordsByUser(@PathVariable Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        User user = optionalUser.get();
        List<Record> records = recordService.getRecordsByUser(user);

        List<RecordDTO> recordDTOS = records.stream().map(RecordDTO::new).toList();
        System.out.println(recordDTOS);

        return ResponseEntity.ok(recordDTOS);
    }

    @PostMapping("/save-generated")
    public ResponseEntity<Map<String, Object>> saveGenerated(@RequestBody RecordDTO recordDTO, HttpServletRequest request) {

        // JWT 토큰에서 사용자 ID 추출
        String token = jwtTokenProvider.getTokenFromCookie(request);
        // System.out.println("token : "+token);
        Long userId = jwtTokenProvider.getIdFromToken(token);
        // System.out.println("userId : "+userId);

        // System.out.println(recordDTO.getResult());

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found."));
        }

        User user = optionalUser.get();
        recordService.createRecord(user, RecordType.GENERATE, recordDTO.getResult());

        return ResponseEntity.ok().body(Map.of("result", "저장 완료됨."));
    }


    @GetMapping("/records")
    public ResponseEntity<Map<Record.RecordType, Long>> getRecordCountsByType() {
        List<Object[]> results = recordRepository.countRecordsByType();

        Map<Record.RecordType, Long> recordCounts = new EnumMap<>(Record.RecordType.class);
        for (Object[] result : results) {
            Record.RecordType type = (Record.RecordType) result[0];
            Long count = (Long) result[1];
            recordCounts.put(type, count);
        }

        return ResponseEntity.ok(recordCounts);
    }
}
