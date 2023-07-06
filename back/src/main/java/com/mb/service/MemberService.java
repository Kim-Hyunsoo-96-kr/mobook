package com.mb.service;

import com.mb.domain.Member;
import com.mb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    public Member addMember(Member member) {
        Member saveMember = memberRepository.save(member);
        return saveMember;
    }

    public Member findByEmail(String email) {
        Member findMember = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사용자입니다."));
        return findMember;
    }

    public Member findById(long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사용자입니다."));
        return findMember;
    }
}
