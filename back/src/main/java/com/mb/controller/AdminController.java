package com.mb.controller;

import com.mb.domain.Member;
import com.mb.dto.Book.req.BookAddDto;
import com.mb.dto.Member.req.MemberSignUpDto;
import com.mb.dto.Util.SecretRequestDto;
import com.mb.service.BookService;
import com.mb.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "관리자 생성", description = "입력값으로 관리자 계정을 생성합니다.")
    @PostMapping("/signUp/secret")
    public ResponseEntity joinSecret(@RequestBody @Valid SecretRequestDto secretRequestDto) {
        return memberService.joinSecret(secretRequestDto);
    }

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
     */
    @Operation(summary = "엑셀 파일로 책 추가", description = "DB에 엑셀 파일에 있는 책을 추가합니다.")
    @PostMapping("/add/excel")
    public ResponseEntity addExcel(@RequestParam("excelFile") MultipartFile mf, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.addBookByExcel(mf, loginMember);
    }

    /**
     * 400 : DB 에러 메세지 O
     * 200 : 성공 : 응답 O
     * 412 : 관리자가 아닌 경우 에러 : 메세지 O
     */
    @Operation(summary = "책 삭제", description = "책을 삭제 상태로 변경합니다.")
    @PostMapping("/delete/book/{bookId}")
    public ResponseEntity addBook(@PathVariable Long bookId, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.deleteBook(bookId, loginMember);
    }

    /**
     * 400 : DB 에러 메세지 O
     * 200 : 성공 : 응답 O
     * 412 : 관리자가 아닌 경우 에러 : 메세지 O
     */
    @Operation(summary = "책 수정", description = "책의 제목과 번호를 수정합니다.")
    @PostMapping("/edit/book/{bookId}")
    public ResponseEntity editBook(@PathVariable Long bookId, @RequestBody BookAddDto bookAddDto ,Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.editBook(bookId, bookAddDto, loginMember);
    }

    /**
     * 500 : DB 에러
     * 200 : 성공 : 응답 O
     * 412 : 관리자가 아닌 경우 에러 : 메세지 O
     */
    @Operation(summary = "관리자 : 전체 대여/반납 기록 보기", description = "bookLog의 데이터를 봅니다.")
    @GetMapping("bookLog")
    public ResponseEntity bookLog(Authentication authentication, @RequestParam(name = "searchText") String searchText, @RequestParam(name = "page", defaultValue = "1") Integer page){
        Member loginMember = getLoginMember(authentication);
        return bookService.bookLog(loginMember, searchText, page);
    }

    /**
     * 500 : 기타 에러
     * 200 : 성공 : 응답 O
     * 412 : 관리자가 아닌 경우 에러 : 메세지 O
     */
    @Operation(summary = "관리자 : 현재 대여 중인 책 내역 보기", description = "bookLog의 데이터 중 대여 중인 리스트를 봅니다.")
    @GetMapping("rentBookLog")
    public ResponseEntity rentBookLog(Authentication authentication, @RequestParam(name = "searchText") String searchText, @RequestParam(name = "page", defaultValue = "1") Integer page){
        Member loginMember = getLoginMember(authentication);
        return bookService.rentBookLog(loginMember, searchText, page);
    }

    /**
     * 500 : 기타 에러
     * 200 : 성공 : 응답 O
     * 412 : 관리자가 아닌 경우 에러 : 메세지 O
     */
    @Operation(summary = "관리자 : 신청한 책 내역 보기", description = "bookRequest의 데이터를 봅니다.")
    @GetMapping("requestBookLog")
    public ResponseEntity requestBookLog(Authentication authentication, @RequestParam(name = "searchText") String searchText, @RequestParam(name = "page", defaultValue = "1") Integer page){
        Member loginMember = getLoginMember(authentication);
        return bookService.requestBookLog(loginMember, searchText, page);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 500 : 로그가 없는 경우 : 메세지 X
     * 412 : 관리자가 아닌 경우 : 메세지 O
     */
    @Operation(summary = "관리자 : 신청 책 처리 완료", description = "해당 책을 처리완료 상태로 DB에 저장합니다.")
    @PostMapping("/request/complete/{bookRequestId}")
    public ResponseEntity requestComplete(@PathVariable Long bookRequestId, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.requestComplete(loginMember, bookRequestId);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 대여한 책이 아닌 경우 : 메세지 O
     * 500 : 로그가 없는 경우 : 메세지 X
     * 412 : 관리자가 아닌 경우 : 메세지 O
     */
    @Operation(summary = "관리자 : 책 반납", description = "해당 책을 대여가능 상태로 DB에 저장합니다.")
    @PostMapping("/return/{bookNumber}")
    public ResponseEntity bookReturn(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.adminReturnBook(loginMember, bookNumber);
    }

    /**
     * 200 : 성공 : 메세지 O
     * 400 : 대여 중인 책이 아닌 경우 : 메세지 O
     */
    @PostMapping("/extend/{bookNumber}")
    public ResponseEntity extendPeriod(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        return bookService.adminExtendPeriod(loginMember, bookNumber);
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
