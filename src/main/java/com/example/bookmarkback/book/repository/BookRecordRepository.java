package com.example.bookmarkback.book.repository;

import com.example.bookmarkback.book.entity.BookRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRecordRepository extends JpaRepository<BookRecord, Long> {

    List<BookRecord> findByMemberId(Long memberId);

    boolean existsByBook_IsbnAndMember_Id(String isbn, Long memberId);
}
