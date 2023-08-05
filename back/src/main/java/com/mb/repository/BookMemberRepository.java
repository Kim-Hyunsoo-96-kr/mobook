package com.mb.repository;

import com.mb.domain.BookHistory;
import com.mb.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookMemberRepository extends JpaRepository<BookHistory, Long> {
    List<BookHistory> findByMember(Member member);
}
