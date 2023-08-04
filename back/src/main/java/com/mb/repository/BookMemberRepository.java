package com.mb.repository;

import com.mb.domain.BookMember;
import com.mb.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookMemberRepository extends JpaRepository<BookMember, Long> {
    List<BookMember> findByMember(Member member);
}
