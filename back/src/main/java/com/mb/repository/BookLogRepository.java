package com.mb.repository;

import com.mb.domain.Book;
import com.mb.domain.BookLog;
import com.mb.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookLogRepository extends JpaRepository<BookLog, Long>,  QuerydslPredicateExecutor<BookLog>{
    List<BookLog> findByMember(Member member);

    Optional<BookLog> findByMemberAndBookAndStatus(Member member, Book book, String status);

    List<BookLog> findByMemberAndStatus(Member member, String bookStatus);

    List<BookLog> findByReturnDate(String returnDate);

    List<BookLog> findByStatus(String bookStatus);

    Optional<BookLog> findByBookAndStatus(Book book, String bookStatus);

//    List<BookLog> findByMemberAndBookNameContaining(Member loginMember, String searchText, Pageable pageable);
}
