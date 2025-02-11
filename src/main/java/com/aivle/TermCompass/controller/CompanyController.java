package com.aivle.TermCompass.controller;

import com.aivle.TermCompass.domain.Company;
import com.aivle.TermCompass.dto.SiteDTO;
import com.aivle.TermCompass.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/site")
@Controller
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("")
    public ResponseEntity<List<SiteDTO>> getCompanies() {
        List<Company> companyList = companyService.findAllCompany();

        List<SiteDTO> siteDTOList = companyList.stream()
                .map(SiteDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(siteDTOList);
    }

    @GetMapping("/{companyName}")
    public ResponseEntity<SiteDTO> getTerms(@PathVariable String companyName) {
        Company company = companyService.findByName(companyName);

        SiteDTO siteDTO = new SiteDTO(company);

        return ResponseEntity.ok(siteDTO);
    }
}
