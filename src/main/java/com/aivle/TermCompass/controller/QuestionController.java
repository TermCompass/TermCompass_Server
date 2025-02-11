package com.aivle.TermCompass.controller;

import com.aivle.TermCompass.domain.FileEntity;
import com.aivle.TermCompass.domain.Question;
import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.dto.QuestionDetailDTO;
import com.aivle.TermCompass.dto.QuestionListDTO;
import com.aivle.TermCompass.service.FileService;
import com.aivle.TermCompass.service.QuestionService;
import com.aivle.TermCompass.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/board")
@RequiredArgsConstructor
@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;
    private final FileService fileService;

    @GetMapping("/list")
    public ResponseEntity<Page<QuestionListDTO>> questionList(@RequestParam(value="page", defaultValue = "0") int page) {
        Page<Question> questionPage = questionService.getList(page, Sort.by(Sort.Direction.DESC, "id"));
        Page<QuestionListDTO> dtoPage = questionPage.map(q ->
                new QuestionListDTO(q.getId(), q.getTitle(), q.getUser().getName(), q.getCreated_at()));


        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<QuestionDetailDTO> questionDetail(@PathVariable("id") Long id) {
        QuestionDetailDTO questionDetailDTO = questionService.getQuestionDetail(id);

        return ResponseEntity.ok(questionDetailDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<String> questionCreate(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpServletRequest httpServletRequest
    ) {
        if (title.trim().isEmpty() || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("제목과 내용을 입력해야 합니다.");
        }

        String email = (String) httpServletRequest.getAttribute("email");
        if (email == null) {
            return ResponseEntity.badRequest().body("인증되지 않은 사용자입니다.");
        }

        User user = userService.findByEmail(email);
        System.out.println(user.getId() + user.getName());

        // 파일이 있을 경우 처리
        if (file != null && !file.isEmpty()) {
            try {
                FileEntity savedFile = fileService.saveFile(file);
                this.questionService.create(title, content, user, savedFile);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 중 오류 발생");
            }
        } else {
            this.questionService.create(title, content, user, null);
        }

        return ResponseEntity.ok("게시글이 생성되었습니다.");
    }
}
