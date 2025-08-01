package com.example.bookmarkback.book.entity;

import com.example.bookmarkback.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.Getter;

@Entity
@Getter
public class BookLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_record_id")
    private BookRecord bookRecord;

    @Column(nullable = false)
    private Long pageStart;

    @Column(nullable = false)
    private Long pageEnd;

    @Column(nullable = false)
    private LocalDate readingDate;

    @Column(nullable = false)
    private Long readingTime;

}
