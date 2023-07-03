package com.mb.service;

import com.mb.domain.Member;
import com.mb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    public Member addMember(Member member) {
        Member saveMember = memberRepository.save(member);
        return saveMember;
    }
}
