package com.aivle.TermCompass.controller;

import com.aivle.TermCompass.domain.DailyStats;
import com.aivle.TermCompass.repository.DailyStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class DailyStatsController {
    private final DailyStatsRepository dailyStatsRepository;

    // ✅ 1. 일별 방문자 수 조회 API
    @GetMapping("/logins")
    public ResponseEntity<List<DailyStatsDto>> getDailyLoginStats() {
        List<DailyStats> stats = dailyStatsRepository.findAllByOrderByDateAsc();

        List<DailyStatsDto> response = stats.stream()
                .map(stat -> new DailyStatsDto(stat.getDate(), stat.getLoginCount()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ✅ 2. 일별 생성, 검토, 챗봇 3선 그래프 API
    @GetMapping("/records")
    public ResponseEntity<List<DailyRecordStatsDto>> getDailyRecordStats() {
        List<DailyStats> stats = dailyStatsRepository.findAllByOrderByDateAsc();

        List<DailyRecordStatsDto> response = stats.stream()
                .map(stat -> new DailyRecordStatsDto(
                        stat.getDate(),
                        stat.getGenerateCount(),
                        stat.getReviewCount(),
                        stat.getChatCount()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private record DailyStatsDto(LocalDate date, int loginCount) {}

    // ✅ DTO 클래스 (날짜별 생성, 검토, 챗봇 데이터)
    private record DailyRecordStatsDto(LocalDate date, int generateCount, int reviewCount, int chatCount) {}
}
