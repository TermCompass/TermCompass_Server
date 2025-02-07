package com.aivle.TermCompass.service;

import com.aivle.TermCompass.domain.FileEntity;
import com.aivle.TermCompass.domain.Question;
import com.aivle.TermCompass.domain.User;
import com.aivle.TermCompass.dto.QuestionDetailDTO;
import com.aivle.TermCompass.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public Page<Question> getList(int page, Sort sort) {
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Question> questionPage = questionRepository.findAll(pageable);

        questionPage.getContent().forEach(q -> {
            Hibernate.initialize(q.getUser());
        });

        return questionPage;
    }

    public Question findById(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);

        return optionalQuestion.orElse(null);
    }

    @Transactional
    public QuestionDetailDTO getQuestionDetail(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        // User 객체를 트랜잭션 내에서 로딩
        User user = question.getUser();
        return new QuestionDetailDTO(question, user);
    }

    public void create(String title, String content, User user, FileEntity file) {
        Question q = new Question();

        q.setTitle(title);
        q.setContent(content);
        q.setCreated_at(LocalDateTime.now());
        q.setUser(user);
        q.setFile(file);

        this.questionRepository.save(q);
    }
}
