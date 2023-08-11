package com.mb.controller;

import com.mb.domain.*;
import com.mb.dto.*;
import com.mb.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mb.enum_.BookStatus.*;

@Tag(name="BookController", description = "책 컨트롤러")
@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final MemberService memberService;
    private final BookRequestService bookRequestService;

    /**
     * 200 : 성공 : 메세지 O
     * */
    @Operation(summary = "책 추가", description = "DB에 책을 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity addBook(@RequestBody BookAddDto bookAddDto){
        return bookService.addBook(bookAddDto);
    }

    /**
     * 400 : 서비스 오류 : 메세지 O
     * 200 : 성공 : 메세지 O
     * */
    @Operation(summary = "엑셀 파일로 책 추가", description = "DB에 엑셀 파일에 있는 책을 추가합니다.")
    @PostMapping("/add/excel")
    public ResponseEntity addExcel(@RequestParam("excelFile") MultipartFile mf){
        return bookService.addBookByExcel(mf);
    }

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
    @PostMapping("/recommend/{bookNumber}")
    public ResponseEntity bookRecommend(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        MessageDto messageDto = bookService.recommendBook(loginMember, bookNumber);

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    @PostMapping("/recommend/cancel/{bookNumber}")
    public ResponseEntity bookRecommendCancel(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        MessageDto messageDto = bookService.recommendCancelBook(loginMember, bookNumber);

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }
    @Operation(summary = "책 반납", description = "해당 책을 대여가능 상태로 DB에 저장합니다.")
    @PostMapping("/return/{bookNumber}")
    public ResponseEntity bookReturn(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        MessageDto messageDto = bookService.returnBook(loginMember, bookNumber);

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    @Operation(summary = "책 검색", description = "검색값과 일치하는 제목의 책 리스트를 가져옵니다.")
    @GetMapping("/search")
    public ResponseEntity bookSearch(@RequestParam(name = "searchText") String searchText, @RequestParam(name = "page", defaultValue = "1") Integer page, Pageable pageable){
        pageable = PageRequest.of(page, 10, Sort.by("bookId").descending());
        List<Book> bookList = bookService.getBookListByKeyword(searchText, pageable.withPage(page));
        Integer totalCnt = bookService.getTotalCntBySearchText(searchText) + 1 ;

        BookListResponseDto bookListResponseDto = new BookListResponseDto();
        bookListResponseDto.setBookList(bookList);
        bookListResponseDto.setTotalCnt(totalCnt);
        return new ResponseEntity(bookListResponseDto, HttpStatus.OK);
    }

    @PostMapping("/request/complete/{bookRequestId}")
    public ResponseEntity requestComplete(@PathVariable Long bookRequestId){
        BookRequest bookRequest = bookRequestService.findById(bookRequestId);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bookRequest.setCompleteDate(today.format(formatter));
        bookRequest.setStatus(RequestComplete.getBookStatus());
        bookRequestService.save(bookRequest);
        MessageDto messageDto = new MessageDto();
        messageDto.setMessage("성공적으로 처리완료했습니다.");

        return new ResponseEntity(messageDto, HttpStatus.OK);
    }

    @PostMapping("/extend/{bookNumber}")
    public ResponseEntity extendPeriod(@PathVariable String bookNumber, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        MessageDto messageDto = bookService.extendPeriod(loginMember, bookNumber);

        return new ResponseEntity(messageDto, HttpStatus.OK);
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
