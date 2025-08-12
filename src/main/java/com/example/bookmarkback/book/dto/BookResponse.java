package com.example.bookmarkback.book.dto;

import com.example.bookmarkback.book.entity.Book;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookResponse(
        Long id,
        String isbn,
        String title,
        String contents,
        String author,
        Double rating,
        Long page,
        String imageUrl,
        String publisher,
        LocalDate publishDate
) {
    public static BookResponse response(Map<String, Object> book) {
        return BookResponse.builder()
                .isbn(book.get("isbn13").toString())
                .title(book.get("title").toString())
                .contents(book.get("description").toString())
                .author(book.get("author").toString())
                .imageUrl(book.get("cover").toString())
                .publisher(book.get("publisher").toString())
                .publishDate(LocalDate.parse(book.get("pubDate").toString()))
                .build();
    }

    public static BookResponse response(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .contents(book.getContents())
                .author(book.getAuthor())
                .rating(book.getRating())
                .page(book.getPage())
                .imageUrl(book.getImageUrl())
                .publisher(book.getPublisher())
                .publishDate(book.getPublishDate())
                .build();
    }
}
