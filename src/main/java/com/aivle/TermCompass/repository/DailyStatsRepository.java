package com.aivle.TermCompass.repository;

import com.aivle.TermCompass.domain.DailyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {
    Optional<DailyStats> findByDate(LocalDate date);

    List<DailyStats> findAllByOrderByDateAsc();

}
