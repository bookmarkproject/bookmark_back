package com.example.bookmarkback.book.repository;

import com.example.bookmarkback.book.entity.BookLog;
import com.example.bookmarkback.book.entity.BookRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookLogRepository extends JpaRepository<BookLog, Long> {

    List<BookLog> findByBookRecord(BookRecord bookRecord);
}
