package com.mb.service;

import com.mb.domain.Member;
import com.mb.dto.*;
import com.mb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";

    @Transactional
    public MemberSignUpResponseDto addMember(MemberSignUpDto memberSignUpDto) {
        String name = memberSignUpDto.getName();
        String email = memberSignUpDto.getEmail();
        String password = memberSignUpDto.getPassword();

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setIsAdmin(false);
        member.setName(name);

        memberRepository.save(member);

        MemberSignUpResponseDto memberSignUpResponseDto = new MemberSignUpResponseDto();

        memberSignUpResponseDto.setEmail(member.getEmail());
        memberSignUpResponseDto.setName(member.getName());

        return memberSignUpResponseDto;
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

    @Transactional
    public MessageDto findPassword(FindPasswordDto findPasswordDto) {
        MessageDto messageDto = new MessageDto();
        Member member =  memberRepository.findByEmailAndName(findPasswordDto.getEmail(), findPasswordDto.getName());
        String newPassword = generateRandomPassword();
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        messageDto.setMessage("이메일로 새로운 비밀번호를 발송했습니다.");
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                String[] receiveArray = {member.getEmail()};
                Map<String, Object> model = new HashMap<>();
                model.put("newPassword", newPassword);
                try {
                    mailService.sendHtmlEmail(receiveArray, "[MOBOOK1.0]새로운 비밀번호 안내", "findPassword.html", model);
                } catch (Exception e) {
                    messageDto.setMessage("메일 발송 관련 오류");
                }
            }
        });
        return messageDto;
    }

    public static String generateRandomPassword() {
        String characters = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
        int minLength = 8;
        int maxLength = 20;

        SecureRandom random = new SecureRandom();
        int passwordLength = random.nextInt(maxLength - minLength + 1) + minLength;

        List<Character> passwordChars = new ArrayList<>();
        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            passwordChars.add(characters.charAt(randomIndex));
        }

        StringBuilder password = new StringBuilder();
        for (Character character : passwordChars) {
            password.append(character);
        }

        return password.toString();
    }
}
