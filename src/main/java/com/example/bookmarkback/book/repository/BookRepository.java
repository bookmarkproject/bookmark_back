package com.example.bookmarkback.book.repository;

import com.example.bookmarkback.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
