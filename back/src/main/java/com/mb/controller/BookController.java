package com.mb.controller;

import com.mb.domain.*;
import com.mb.dto.*;
import com.mb.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name="BookController", description = "책 컨트롤러")
@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final MemberService memberService;

    /**
     * 200 : 성공 : 메세지 O
     * 500 : 에러 : 메일 관련 오류 : 메세지 X
     * */
    @Operation(summary = "책 요청", description = "사용자가 원하는 책을 관리자에게 요청합니다.")
    @PostMapping("/request")
    public ResponseEntity bookRequest(@RequestBody BookRequestDto bookRequestDto, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.request(loginMember, bookRequestDto);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 에러 : 대여 불가능한 경우 : 메세지 O
     * */
    @Operation(summary = "책 대여", description = "해당 책을 대여불가 상태로 DB에 저장합니다.")
    @PostMapping("/rent/{bookNumber}")
    public ResponseEntity bookRent(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.rentBook(loginMember, bookNumber);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 이미 추천한 경우 : 메세지 O
     */
    @PostMapping("/recommend/{bookNumber}")
    public ResponseEntity bookRecommend(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.recommendBook(loginMember, bookNumber);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 추천한 책이 아닌 경우 : 메세지 O
     * 500 : 로그가 없는 경우 : 메세지 X
     */
    @PostMapping("/recommend/cancel/{bookNumber}")
    public ResponseEntity bookRecommendCancel(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.recommendCancelBook(loginMember, bookNumber);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 대여한 책이 아닌 경우 : 메세지 O
     * 500 : 로그가 없는 경우 : 메세지 X
     */
    @Operation(summary = "책 반납", description = "해당 책을 대여가능 상태로 DB에 저장합니다.")
    @PostMapping("/return/{bookNumber}")
    public ResponseEntity bookReturn(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.returnBook(loginMember, bookNumber);
    }

    /**
     * 200 : 성공 : 메세지 X, 응답 O
     */
    @Operation(summary = "책 검색", description = "검색값과 일치하는 제목의 책 리스트를 가져옵니다.")
    @GetMapping("/search")
    public ResponseEntity bookSearch(@RequestParam(name = "searchText") String searchText, @RequestParam(name = "page", defaultValue = "1") Integer page, Pageable pageable){
        return bookService.bookSearch(searchText, page, pageable);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 500 : 로그가 없는 경우 : 메세지 X
     */
    @PostMapping("/request/complete/{bookRequestId}")
    public ResponseEntity requestComplete(@PathVariable Long bookRequestId){
        return bookService.requestComplete(bookRequestId);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 대여 중인 책이 아닌 경우 : 메세지 O
     */
    @PostMapping("/extend/{bookNumber}")
    public ResponseEntity extendPeriod(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.extendPeriod(loginMember, bookNumber);
    }

    private Member getLoginMember(Authentication authentication) {
        if(authentication == null){
            return new Member();
        }
        Long memberId = (Long) authentication.getPrincipal();
        Member loginMember = memberService.findById(memberId);
        return loginMember;
    }

}
