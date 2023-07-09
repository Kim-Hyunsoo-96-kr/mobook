package com.mb.controller;

import com.mb.domain.Member;
import com.mb.domain.RefreshToken;
import com.mb.dto.*;
import com.mb.service.MemberService;
import com.mb.service.RefreshTokenService;
import com.mb.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.secretKey}")
    public String accessSecretKey;
    @Value("${jwt.refreshKey}")
    public String refreshSecretKey;

    @PostMapping("/signUp")
    public ResponseEntity join(@RequestBody @Valid MemberSignUpDto memberSignUpDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        String name = memberSignUpDto.getName();
        String email = memberSignUpDto.getEmail();
        String password = memberSignUpDto.getPassword();

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(password);
        member.setIsAdmin(false);
        member.setName(name);

        Member saveMember = memberService.addMember(member);

        MemberSignUpResponseDto memberSignUpResponseDto = new MemberSignUpResponseDto();

        memberSignUpResponseDto.setEmail(saveMember.getEmail());
        memberSignUpResponseDto.setName(saveMember.getName());

        return new ResponseEntity(memberSignUpResponseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDto memberLoginDto, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Member findMember = memberService.findByEmail(memberLoginDto.getEmail());

        if (!findMember.getPassword().equals(memberLoginDto.getPassword())) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        String accessToken = JwtUtil.createAccessToken(findMember, accessSecretKey);
        String refreshToken = JwtUtil.createRefreshToken(findMember, refreshSecretKey);

        RefreshToken saveRefreshToken = new RefreshToken();
        saveRefreshToken.setMemberId(findMember.getMemberId());
        saveRefreshToken.setValue(refreshToken);

        refreshTokenService.addToken(saveRefreshToken);
        MemberLoginResponseDto memberLoginResponseDto = new MemberLoginResponseDto();
        memberLoginResponseDto.setName(findMember.getName());
        memberLoginResponseDto.setAccessToken(accessToken);
        memberLoginResponseDto.setRefreshToken(refreshToken);

        return new ResponseEntity(memberLoginResponseDto, HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenDto.getRefreshToken());
        Long memberId = JwtUtil.getMemberId(refreshToken.getValue(), refreshSecretKey);
        Member member = memberService.findById(memberId);

        String accessToken = JwtUtil.createAccessToken(member, accessSecretKey);

        MemberLoginResponseDto memberLoginResponseDto = new MemberLoginResponseDto();
        memberLoginResponseDto.setName(member.getName());
        memberLoginResponseDto.setAccessToken(accessToken);
        memberLoginResponseDto.setRefreshToken(refreshToken.getValue());

        return new ResponseEntity(memberLoginResponseDto, HttpStatus.OK);
    }


}
