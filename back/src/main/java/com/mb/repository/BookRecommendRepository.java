package com.mb.repository;

import com.mb.domain.Book;
import com.mb.domain.BookLog;
import com.mb.domain.BookRecommend;
import com.mb.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRecommendRepository extends JpaRepository<BookRecommend, Long>, QuerydslPredicateExecutor<BookRecommend> {
    Optional<BookRecommend> findByMemberAndBook(Member member, Book book);

    List<BookRecommend> findByMember(Member member);
}
