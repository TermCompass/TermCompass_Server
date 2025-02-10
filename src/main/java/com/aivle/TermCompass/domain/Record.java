package com.aivle.TermCompass.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column
    private RecordType record_type;

    @Column
    private String title;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String result;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Request> requests = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    
    public enum RecordType {
        REVIEW, GENERATE, CHAT
    }

    // result 제외 조회용 생성자 오버로딩
    public Record(Long id, User user, RecordType record_type, String title, LocalDateTime createdDate) {
        this.id = id;
        this.user = user;
        this.record_type = record_type;
        this.title = title;
        this.createdDate = createdDate;
    }
    
    // 기본 생성자
    public Record() {
    }
}
