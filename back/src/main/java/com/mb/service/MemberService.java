package com.mb.service;

import com.mb.domain.Member;
import com.mb.dto.ChangePasswordDto;
import com.mb.dto.MessageDto;
import com.mb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

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

    public String[] findAllMemberMailReceiveArray() {
        List<Member> allMemberList = memberRepository.findAll();
        List<String> emailList = new ArrayList();
        for (Member member : allMemberList) {
            String email = member.getEmail();
            emailList.add(email);
        }
        String[] receiverList = emailList.toArray(new String[0]);
        return receiverList;
    }

    public String[] findAllAdminMailReceiveArray() {
        List<Member> allAdminList = memberRepository.findByIsAdmin(true);
        List<String> emailList = new ArrayList();
        for (Member member : allAdminList) {
            String email = member.getEmail();
            emailList.add(email);
        }
        String[] receiverList = emailList.toArray(new String[0]);
        return receiverList;
    }
}
