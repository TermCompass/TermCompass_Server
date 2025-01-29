package com.aivle.TermCompass.repository;

import com.aivle.TermCompass.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
