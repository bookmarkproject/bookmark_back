package com.example.bookmarkback.book.entity;

import com.example.bookmarkback.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@ToString
public class Book extends BaseEntity {

    public Book() {
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Book(String title, String author, String publisher, LocalDate publishDate, String contents, Long page,
                String imageUrl, String isbn, Double rating) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.contents = contents;
        this.page = page;
        this.imageUrl = imageUrl;
        this.isbn = isbn;
        this.rating = rating;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private LocalDate publishDate;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private Long page;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private Double rating;
}
