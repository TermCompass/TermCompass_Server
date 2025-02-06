package com.aivle.TermCompass.service;

import com.aivle.TermCompass.domain.Answer;
import com.aivle.TermCompass.domain.Question;
import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    public void create(Question question, String content, User user) {
        Answer answer = new Answer();

        answer.setContent(content);
        answer.setQuestion(question);
        answer.setCreated_at(LocalDateTime.now());
        answer.setUser(user);
        answerRepository.save(answer);
    }
}
