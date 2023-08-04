package com.mb.repository;

import com.mb.domain.Book;
import com.mb.domain.BookMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMemberRepository extends JpaRepository<BookMember, Long> {
}
