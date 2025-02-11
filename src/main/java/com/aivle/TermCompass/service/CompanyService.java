package com.aivle.TermCompass.service;

import com.aivle.TermCompass.domain.Company;
import com.aivle.TermCompass.domain.TermList;
import com.aivle.TermCompass.repository.CompanyRepository;
import com.aivle.TermCompass.repository.TermListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final TermListRepository termListRepository;

    @Transactional
    public List<Company> findAllCompany() {
        return companyRepository.findAll();
    }

    public List<TermList> findTermsByCompany(Company company) {
        return termListRepository.findByCompany(company);
    }

    public Company findByName(String name) {
        Optional<Company> optionalCompany = companyRepository.findByName(name);

        return optionalCompany.orElse(null);
    }

    public Company findByLink(String link) {
        Optional<Company> optionalCompany = companyRepository.findByLink(link);

        return optionalCompany.orElse(null);
    }
}
