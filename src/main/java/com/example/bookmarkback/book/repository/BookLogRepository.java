package com.example.bookmarkback.book.repository;

import com.example.bookmarkback.book.entity.BookLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookLogRepository extends JpaRepository<BookLog, Long> {
}
