package com.aivle.TermCompass.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String handleRequest(HttpServletRequest request, Model model) {

        String host = request.getHeader("Host");

        if (host != null && host.startsWith("admin.")) {
            return "admin-login.html"; // admin.html 반환
        } else {
            return "index.html"; // index.html 반환
        }
    }
    
    @GetMapping(value = "/board")
    public String getBoardPage() {
        return "board.html";
    }    
    
    @GetMapping(value = "/boardDetail")
    public String getBoardDetailPage(@RequestParam("id") Long id, Model model) {
        // id 값을 모델에 추가하여 뷰에서 사용할 수 있도록 합니다.
        model.addAttribute("postId", id);
        return "boardDetail.html";
    }

    @GetMapping(value = "/boardWrite")
    public String getBoardWritePage() {
        return "boardWrite.html";
    }
    
    @GetMapping(value = "/create-terms")
    public String getCreateTermsPage() {
        return "create-terms.html";
    }
    
    @GetMapping(value = "/my-page")
    public String getMyPage() {
        return "my-page.html";
    }  

    @GetMapping(value = "/review-request")
    public String getReviewRequestPage() {
        return "review-request.html";
    }

    @GetMapping(value = "/site-analysis")
    public String getSiteAnalysisPage() {
        return "site-analysis.html";
    }

    @GetMapping(value = "/site-detail")
    public String getSiteDetailPage() {
        return "site-detail.html";
    }

}
