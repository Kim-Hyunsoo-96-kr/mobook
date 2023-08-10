package com.mb.controller;

import com.mb.domain.*;
import com.mb.dto.*;
import com.mb.service.*;
import com.mb.util.BookLogUtil;
import com.mb.util.JwtUtil;
import com.mb.util.RentBookLog;
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

import static com.mb.enum_.BookStatus.InRental;

@Tag(name="MemberController", description = "멤버 컨트롤러")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final BookService bookService;
    private final BookLogService bookLogService;
    private final BookRecommendService bookRecommendService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secretKey}")
    public String accessSecretKey;
    @Value("${jwt.refreshKey}")
    public String refreshSecretKey;

    @Operation(summary = "회원 가입", description = "입력값으로 회원가입을 요청합니다.")
    @PostMapping("/signUp")
    public ResponseEntity join(@RequestBody @Valid MemberSignUpDto memberSignUpDto, Authentication authentication) {

        Member loginMember = getLoginMember(authentication);
        if(loginMember.getIsAdmin()){
            MemberSignUpResponseDto memberSignUpResponseDto = memberService.addMember(memberSignUpDto);
            return new ResponseEntity(memberSignUpResponseDto, HttpStatus.CREATED);
        } else {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessage("관리자가 아닙니다.");
            return new ResponseEntity(messageDto, HttpStatus.BAD_REQUEST);
        }

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

        List<BookLog> bookLogList = bookLogService.findBookLogByMemberId(loginMember);
        List<BookLogUtil> bookLogUtilList = new ArrayList();
        for (BookLog bookLog : bookLogList) {
            String status = bookLog.getStatus();
            String bookName = bookService.findById(bookLog.getBook().getBookId()).getBookName();
            String bookNumber = bookService.findById(bookLog.getBook().getBookId()).getBookNumber();
            String regDate = bookLog.getRegDate();
            BookLogUtil bookLogUtil = new BookLogUtil(bookName, bookNumber, status, regDate);
            bookLogUtilList.add(bookLogUtil);
        }

        List<RentBookLog> rentBookLogList = new ArrayList();
        List<BookLog> bookInRendtalLogList =  bookLogService.findByMemberAndStatus(loginMember, InRental);
        for (BookLog bookLog : bookInRendtalLogList) {
            Book rentBook = bookLog.getBook();
            RentBookLog rentBookLog = new RentBookLog(rentBook.getBookNumber(), rentBook.getBookName(),
                    rentBook.getRecommend(), bookLog.getRegDate(), bookLog.getReturnDate());
            rentBookLogList.add(rentBookLog);
        }

        List<BookRecommend> bookRecommendList = bookRecommendService.findByMember(loginMember);
        List<Book> likeBookList = new ArrayList();
        for (BookRecommend bookRecommend : bookRecommendList) {
            Book book = bookRecommend.getBook();
            likeBookList.add(book);
        }
        myBookResponseDto.setBookLogList(bookLogUtilList);
        myBookResponseDto.setRentBook(rentBookLogList);
        myBookResponseDto.setRecommendBook(likeBookList);
        return new ResponseEntity(myBookResponseDto, HttpStatus.OK);
    }

    @GetMapping("/myBookLog")
    public ResponseEntity myBookLog(Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        BookLogResponseDto bookLogResponseDto = new BookLogResponseDto();
        List<BookLog> bookLogList = bookLogService.findBookLogByMemberId(loginMember);
        List<BookLogUtil> bookLogUtilList = new ArrayList();
        for (BookLog bookLog : bookLogList) {
            String status = bookLog.getStatus();
            String bookName = bookService.findById(bookLog.getBook().getBookId()).getBookName();
            String bookNumber = bookService.findById(bookLog.getBook().getBookId()).getBookNumber();
            String regDate = bookLog.getRegDate();
            BookLogUtil bookLogUtil = new BookLogUtil(bookName, bookNumber, status, regDate);
            bookLogUtilList.add(bookLogUtil);
        }
        bookLogResponseDto.setBookLogList(bookLogUtilList);
        return new ResponseEntity(bookLogResponseDto, HttpStatus.OK);
    }

    @GetMapping("/myRentBook")
    public ResponseEntity myRentBookList(Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        RentBookLogResponseDto rentBookLogResponseDto = new RentBookLogResponseDto();
        List<RentBookLog> rentBookLogList = new ArrayList();
        List<BookLog> bookInRendtalLogList =  bookLogService.findByMemberAndStatus(loginMember, InRental);
        for (BookLog bookLog : bookInRendtalLogList) {
            Book rentBook = bookLog.getBook();
            RentBookLog rentBookLog = new RentBookLog(rentBook.getBookNumber(), rentBook.getBookName(),
                    rentBook.getRecommend(), bookLog.getRegDate(), bookLog.getReturnDate());
            rentBookLogList.add(rentBookLog);
        }
        rentBookLogResponseDto.setRentBook(rentBookLogList);
        return new ResponseEntity(rentBookLogResponseDto, HttpStatus.OK);
    }

    @GetMapping("/myRecommendBook")
    public ResponseEntity myRecommendBookList(Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        RecommendBookLogResponseDto recommendBookLogResponseDto = new RecommendBookLogResponseDto();
        List<BookRecommend> bookRecommendList = bookRecommendService.findByMember(loginMember);
        List<Book> recommendBookList = new ArrayList();
        for (BookRecommend bookRecommend : bookRecommendList) {
            Book book = bookRecommend.getBook();
            recommendBookList.add(book);
        }
        recommendBookLogResponseDto.setRecommendBook(recommendBookList);
        return new ResponseEntity(recommendBookLogResponseDto, HttpStatus.OK);
    }

    @GetMapping("/myRequestBook")
    public ResponseEntity myRequestBook(Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        RequestBookLogResponseDto requestBookLogResponseDto =  bookService.findMyRequesyBookList(loginMember);

        return new ResponseEntity(requestBookLogResponseDto, HttpStatus.OK);
    }

    @PostMapping("/changePw")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePasswordDto, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        MessageDto messageDto = memberService.changePassword(changePasswordDto, loginMember);

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    @PostMapping("/findPw")
    public ResponseEntity findPassword(@RequestBody FindPasswordDto findPasswordDto){
        MessageDto messageDto = memberService.findPassword(findPasswordDto);

        return new ResponseEntity(messageDto, HttpStatus.OK);
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
