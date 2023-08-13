package com.mb.controller;

import com.mb.domain.Member;
import com.mb.dto.BookAddDto;
import com.mb.dto.MemberSignUpDto;
import com.mb.service.BookService;
import com.mb.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final BookService bookService;

    /**
     * 500 : 요청값 에러
     * 412 : 관리자가 아닌 경우 에러 : 메세지 O
     * 201 : 성공 : 메세지 O
     * */
    @Operation(summary = "회원 가입", description = "입력값으로 회원가입을 요청합니다.")
    @PostMapping("/signUp")
    public ResponseEntity join(@RequestBody @Valid MemberSignUpDto memberSignUpDto, Authentication authentication) {
        Member loginMember = getLoginMember(authentication);
        return memberService.join(loginMember, memberSignUpDto);
    }

    /**
     * 500 : 메일 관련 오류 : 메세지 X
     * 200 : 성공 : 메세지 O
     * 412 : 관리자가 아닌 경우 에러 : 메세지 O
     * */
    @Operation(summary = "책 추가", description = "DB에 책을 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity addBook(@RequestBody BookAddDto bookAddDto, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.addBook(bookAddDto, loginMember);
    }

    /**
     * 500 : 메일 관련 오류 : 메세지 X
     * 400 : 서비스 오류 : 메세지 O
     * 200 : 성공 : 메세지 O
     * 412 : 관리자가 아닌 경우 에러 : 메세지 O
     * */
    @Operation(summary = "엑셀 파일로 책 추가", description = "DB에 엑셀 파일에 있는 책을 추가합니다.")
    @PostMapping("/add/excel")
    public ResponseEntity addExcel(@RequestParam("excelFile") MultipartFile mf, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.addBookByExcel(mf, loginMember);
    }

    @Operation(summary = "관리자 : 전체 대여/반납 기록 보기", description = "bookLog의 데이터를 봅니다.")
    @GetMapping("bookLog")
    public ResponseEntity bookLog(Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.bookLog(loginMember);
    }

    private Member getLoginMember(Authentication authentication) {
        if(authentication == null){
            Member member = new Member();
            return member;
        }
        Long memberId = (Long) authentication.getPrincipal();
        Member loginMember = memberService.findById(memberId);
        return loginMember;
    }
}
