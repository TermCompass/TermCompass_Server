package com.aivle.TermCompass.repository;

import com.aivle.TermCompass.domain.Record;
import com.aivle.TermCompass.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findAllByUser(User user);
}
