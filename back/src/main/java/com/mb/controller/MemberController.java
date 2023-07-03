package com.mb.controller;

import com.mb.domain.Member;
import com.mb.dto.MemberJoinDto;
import com.mb.dto.MemberJoinResponseDto;
import com.mb.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/joinTest")
    public ResponseEntity join(@RequestBody MemberJoinDto memberJoinDto){
        String memberId = memberJoinDto.getId();
        String password = memberJoinDto.getPassword();

        Member member = new Member();
        member.setId(memberId);
        member.setEmail("test@test.com");
        member.setPassword(password);
        member.setIsAdmin(false);

        Member saveMember = memberService.addMember(member);

        MemberJoinResponseDto memberJoinResponseDto = new MemberJoinResponseDto();

        memberJoinResponseDto.setMemberId(saveMember.getId());
        memberJoinResponseDto.setPassword(saveMember.getPassword());

        return new ResponseEntity(memberJoinResponseDto, HttpStatus.CREATED);
    }


}
