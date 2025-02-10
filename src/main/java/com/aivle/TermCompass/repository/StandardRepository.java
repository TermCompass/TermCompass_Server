package com.aivle.TermCompass.repository;

import com.aivle.TermCompass.domain.Standard;
import com.aivle.TermCompass.dto.StandardDTO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StandardRepository extends JpaRepository<Standard, Long> {
    @Query("SELECT new com.aivle.TermCompass.dto.StandardDTO(s.id, s.filename) FROM Standard s")
    List<StandardDTO> findIdAndFilename();
}
