package com.mb.repository;

import com.mb.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    List<Member> findByIsAdmin(boolean admin);

    Optional<Member> findByEmailAndName(String email, String name);
}
