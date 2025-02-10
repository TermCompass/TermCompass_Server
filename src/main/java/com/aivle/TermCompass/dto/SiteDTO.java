package com.aivle.TermCompass.dto;

import com.aivle.TermCompass.domain.Company;
import com.aivle.TermCompass.domain.TermList;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SiteDTO {
    private Long id;
    private String name;
    private String logo;
    private Company.Rank rank;
    private String link;
    private List<TermListDTO> termLists;
    private List<String> benefits;
    private List<String> drawbacks;

    public SiteDTO(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.logo = company.getLogo();
        this.rank = company.getRank();
        this.link = company.getLink();
        this.termLists = company.getTermLists() == null ? List.of() : company.getTermLists().stream()
                .map(TermListDTO::new)
                .collect(Collectors.toList());
        this.benefits = this.termLists.stream()
                .filter(term -> term.getEvaluation() == TermList.Evaluation.ADVANTAGE)
                .map(TermListDTO::getSummary)
                .collect(Collectors.toList());
        this.drawbacks = this.termLists.stream()
                .filter(term -> term.getEvaluation() == TermList.Evaluation.DISADVANTAGE)
                .map(TermListDTO::getSummary)
                .collect(Collectors.toList());
    }
}
