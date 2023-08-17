package com.mb.repository;

import com.mb.domain.Book;
import com.mb.domain.BookRecommend;
import com.mb.domain.BookRequest;
import com.mb.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRequestRepository extends JpaRepository<BookRequest, Long>, QuerydslPredicateExecutor<BookRequest> {
    List<BookRequest> findByMember(Member member);
}
