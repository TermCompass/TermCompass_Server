package com.aivle.TermCompass.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping(value = "/")
    public String getIndexPage() {
        System.out.println("==================================================================== GET / ====================================================================");
        return "index.html";
    }

}
