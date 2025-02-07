package com.aivle.TermCompass.repository;

import com.aivle.TermCompass.domain.Record;
import com.aivle.TermCompass.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    @Query("SELECT r FROM Record r LEFT JOIN FETCH r.requests WHERE r.user = :user")
    List<Record> findAllByUser(@Param("user") User user);
}
