package com.aivle.TermCompass.controller;

import com.aivle.TermCompass.domain.Standard;
import com.aivle.TermCompass.dto.StandardDTO;
import com.aivle.TermCompass.repository.StandardRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class StandardController {

    private final StandardRepository standardRepository;
    
    @PostMapping("/standard")
    public ResponseEntity<Object> loadStandards() {
        List<StandardDTO> results = standardRepository.findIdAndFilename();
        // System.out.println("표준 목록");

        // for (StandardDTO standardDTO : results) {
        //     System.out.println("id : "+standardDTO.getId());
        //     System.out.println("filename : "+standardDTO.getFilename());
        // }
        return ResponseEntity.ok(results);
    }

    @PostMapping("/standard/{id}")
    public ResponseEntity<Object> getTargetStandard(@PathVariable Long id) {
        Standard result = standardRepository.findById(id).get();
        
        // System.out.println("표준");
        // System.out.println("id : " + result.getId());
        // System.out.println("filename : " + result.getFilename());

        return ResponseEntity.ok(result);
    }

}
