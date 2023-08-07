package com.mb.service;

import com.mb.domain.Member;
import com.mb.dto.ChangePasswordDto;
import com.mb.dto.MessageDto;
import com.mb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional
    public MessageDto changePassword(ChangePasswordDto changePasswordDto, Member loginMember) {
        MessageDto messageDto = new MessageDto();
        if(passwordEncoder.matches(changePasswordDto.getOldPassword(), loginMember.getPassword())){
            loginMember.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            memberRepository.save(loginMember);
            messageDto.setMessage("비밀번호를 변경했습니다.");
        } else{
            messageDto.setMessage("기존 비밀번호와 일치하지 않습니다.");
        }
        return messageDto;
    }
}
