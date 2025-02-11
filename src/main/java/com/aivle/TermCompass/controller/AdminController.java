package com.aivle.TermCompass.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/admin")
    public String handleRequest(HttpServletRequest request, Model model) {

        String host = request.getHeader("Host");

        if (host != null && host.startsWith("admin.")) {
            return "admin.html"; // admin.html 반환
        } else {
            return "index.html"; // index.html 반환
        }
    }

}
