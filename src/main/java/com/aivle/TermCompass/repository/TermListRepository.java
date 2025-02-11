package com.aivle.TermCompass.repository;

import com.aivle.TermCompass.domain.Company;
import com.aivle.TermCompass.domain.TermList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermListRepository extends JpaRepository<TermList, Long> {
    List<TermList> findByCompany(Company company);
}
