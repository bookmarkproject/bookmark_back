package com.example.bookmarkback.book.repository;

import com.example.bookmarkback.book.entity.BookLogQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookLogQuestionRepository extends JpaRepository<BookLogQuestion, Long> {

    List<BookLogQuestion> findByBookLogId(Long bookLogId);
}
