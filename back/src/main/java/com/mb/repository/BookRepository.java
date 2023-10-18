package com.mb.repository;

import com.mb.domain.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByBookNameContaining(String keyword, Pageable pageable);

    List<Book> findByBookNameContaining(String keyword);

    List<Book> findByRentalMemberId(Long memberId);

    Optional<Book> findByBookNumber(String bookNumber);

    List<Book> findTop5ByOrderByBookIdDesc();
}
