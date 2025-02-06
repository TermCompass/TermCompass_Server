package com.aivle.TermCompass.repository;

import com.aivle.TermCompass.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @EntityGraph(attributePaths = {"user"}) // ✅ user를 미리 로딩
    Page<Question> findAll(Pageable pageable);
}
