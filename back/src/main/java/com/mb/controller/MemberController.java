package com.mb.controller;

import com.mb.domain.Book;
import com.mb.domain.BookMember;
import com.mb.domain.Member;
import com.mb.domain.RefreshToken;
import com.mb.dto.*;
import com.mb.service.BookMemberService;
import com.mb.service.BookService;
import com.mb.service.MemberService;
import com.mb.service.RefreshTokenService;
import com.mb.util.BookLog;
import com.mb.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name="MemberController", description = "멤버 컨트롤러")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final BookService bookService;
    private final BookMemberService bookMemberService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secretKey}")
    public String accessSecretKey;
    @Value("${jwt.refreshKey}")
    public String refreshSecretKey;

    @Operation(summary = "회원 가입", description = "입력값으로 회원가입을 요청합니다.")
    @PostMapping("/signUp")
    public ResponseEntity join(@RequestBody @Valid MemberSignUpDto memberSignUpDto) {

        String name = memberSignUpDto.getName();
        String email = memberSignUpDto.getEmail();
        String password = memberSignUpDto.getPassword();

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setIsAdmin(false);
        member.setName(name);

        Member saveMember = memberService.addMember(member);

        MemberSignUpResponseDto memberSignUpResponseDto = new MemberSignUpResponseDto();

        memberSignUpResponseDto.setEmail(saveMember.getEmail());
        memberSignUpResponseDto.setName(saveMember.getName());

        return new ResponseEntity(memberSignUpResponseDto, HttpStatus.CREATED);
    }
    @Operation(summary = "로그인(Token 필요)", description = "입력값으로 로그인을 합니다.")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDto memberLoginDto){

        Member findMember = memberService.findByEmail(memberLoginDto.getEmail());

        if (!passwordEncoder.matches(memberLoginDto.getPassword(), findMember.getPassword())) {
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
    @Operation(summary = "accessToken 갱신(Token 필요)", description = "refreshToken으로 accessToken을 갱신합니다.")
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

    @Operation(summary = "로그아웃", description = "DB에서 refreshToken을 삭제합니다.")
    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());

        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "마이 페이지(Token 필요)", description = "로그인한 회원의 정보를 조회합니다.")
    @GetMapping("/myPage")
    public ResponseEntity myPage(Authentication authentication){
        Member loginMember = getLoginMember(authentication);

        MyPageResponseDto myPageResponseDto = new MyPageResponseDto();

        List<Book> rentalBookList = bookService.findByRentalMemberId(loginMember.getMemberId());
        for (Book book : rentalBookList) {
            System.out.println(book.getBookName());
            System.out.println(book.getBookId());
        }

        myPageResponseDto.setName(loginMember.getName());
        myPageResponseDto.setEmail(loginMember.getEmail());
        myPageResponseDto.setIsAdmin(loginMember.getIsAdmin());
        myPageResponseDto.setRentalBookList(rentalBookList);

        return new ResponseEntity(myPageResponseDto, HttpStatus.OK);
    }

    @GetMapping("/myBook")
    public ResponseEntity myBook(Authentication authentication){
        Member loginMember = getLoginMember(authentication);

        MyBookResponseDto myBookResponseDto = new MyBookResponseDto();

        List<BookMember> bookMemberList = bookMemberService.findBookLogByMemberId(loginMember);
        List<BookLog> bookLogList = new ArrayList();
        for (BookMember bookMember : bookMemberList) {
            String status = bookMember.getStatus();
            String bookName = bookService.findById(bookMember.getBook().getBookId()).getBookName();
            String bookNumber = bookService.findById(bookMember.getBook().getBookId()).getBookNumber();
            BookLog bookLog = new BookLog(bookName, bookNumber, status);
            bookLogList.add(bookLog);
        }
        List<Book> rentBookList =  bookService.findByRentalMemberId(loginMember.getMemberId());
        myBookResponseDto.setBookLogList(bookLogList);
        myBookResponseDto.setRentBook(rentBookList);
        return new ResponseEntity(myBookResponseDto, HttpStatus.OK);
    }

    private Member getLoginMember(Authentication authentication) {
        if(authentication == null){
            System.out.println("authentication에 아무것도 없음");
            //Todo
            return memberService.findById(1L);
        }
        Long memberId = (Long) authentication.getPrincipal();
        Member loginMember = memberService.findById(memberId);
        return loginMember;
    }

}
