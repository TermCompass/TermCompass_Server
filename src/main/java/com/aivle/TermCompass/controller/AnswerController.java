package com.aivle.TermCompass.controller;

import com.aivle.TermCompass.domain.Question;
import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.dto.AnswerForm;
import com.aivle.TermCompass.service.AnswerService;
import com.aivle.TermCompass.service.QuestionService;
import com.aivle.TermCompass.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PostMapping("/create/{id}")
    public ResponseEntity<String> createAnswer(@PathVariable Long id, @Valid @RequestBody AnswerForm answerForm,
                                               BindingResult bindingResult, HttpServletRequest httpServletRequest
    ) {
        System.out.println("==============================create/23=============================================");
        Question question = this.questionService.findById(id);
        String email = (String) httpServletRequest.getAttribute("email");
        if (email == null) {
            return ResponseEntity.badRequest().body("인증되지 않은 사용자입니다.");
        }

        User user = userService.findByEmail(email);
        System.out.println(user.getId() + user.getName());
        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("입력되지 않은 항목이 존재합니다.");
        }

        this.answerService.create(question, answerForm.getContent(), user);

        return ResponseEntity.ok("답글이 생성되었습니다.");
    }
}
