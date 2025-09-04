package com.example.bookmarkback.book.entity;

import com.example.bookmarkback.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
public class BookLogQuestion extends BaseEntity {

    public BookLogQuestion() {
    }

    public BookLogQuestion(BookLog bookLog, String question, String answer) {
        this.bookLog = bookLog;
        this.question = question;
        this.answer = answer;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_log_id")
    private BookLog bookLog;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

}
