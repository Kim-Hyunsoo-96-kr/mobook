package com.mb.repository;

import com.mb.domain.Book;
import com.mb.domain.BookHeart;
import com.mb.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookHeartRepository extends JpaRepository<BookHeart, Long> {
    Optional<BookHeart> findByMemberAndBook(Member member, Book book);
}
