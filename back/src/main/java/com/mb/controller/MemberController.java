package com.mb.controller;

import com.mb.domain.*;
import com.mb.dto.*;
import com.mb.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name="MemberController", description = "멤버 컨트롤러")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

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
     * 400 : 서비스 에러 : 메세지 O
     * 200 : 성공 : 메세지 O
     * */
    @Operation(summary = "로그인(Token 필요)", description = "입력값으로 로그인을 합니다.")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDto memberLoginDto){
        return memberService.login(memberLoginDto);
    }
    /**
     * 404 : 서비스 에러 : 메세지 O
     * 200 : 성공 : 메세지 x 응답값 O
     * */
    @Operation(summary = "accessToken 갱신(Token 필요)", description = "refreshToken으로 accessToken을 갱신합니다.")
    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        return memberService.refreshToken(refreshTokenDto);
    }

    /**
     * 200 : 성공 : 메세지 O
     */
    @Operation(summary = "로그아웃", description = "DB에서 refreshToken을 삭제합니다.")
    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        return memberService.logout(refreshTokenDto);
    }

    /**
     * 200 : 성공 : 응답 O
     * 400 : 서비스 에러 : 메세지 O
     * */
    @GetMapping("/myBook")
    public ResponseEntity myBook(Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return memberService.myBook(loginMember);
    }

    /**
     * 200 : 성공 : 응답 O
     * */
    @GetMapping("/myRentBook")
    public ResponseEntity myRentBookList(Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return memberService.myRentBook(loginMember);
    }

    /**
     * 200 : 성공 : 응답 O
     * */
    @GetMapping("/myBookLog")
    public ResponseEntity myBookLog(Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return memberService.myBookLog(loginMember);
    }

    /**
     * 200 : 성공 : 응답 O
     * */
    @GetMapping("/myRecommendBook")
    public ResponseEntity myRecommendBookList(Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return memberService.myRecommendBook(loginMember);
    }

    /**
     * 200 : 성공 : 메세지 O
     * */
    @PostMapping("/changePw")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePasswordDto, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return memberService.changePw(loginMember, changePasswordDto);
    }

    /**
     * 200 : 성공 : 응답 O
     * */
    @PostMapping("/findPw")
    public ResponseEntity findPassword(@RequestBody FindPasswordDto findPasswordDto){
        return memberService.findPassword(findPasswordDto);
    }

    /**
     * 200 : 성공 : 응답 O
     * */
    @GetMapping("/myRequestBook")
    public ResponseEntity myRequestBook(Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return memberService.myRequestBook(loginMember);
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
