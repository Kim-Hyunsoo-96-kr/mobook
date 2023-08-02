package com.mb.controller;

import com.mb.domain.Book;
import com.mb.domain.Member;
import com.mb.dto.*;
import com.mb.service.BookService;
import com.mb.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name="BookController", description = "책 컨트롤러")
@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final MemberService memberService;

    @Operation(summary = "책 추가", description = "DB에 책을 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity addBook(@RequestBody BookAddDto bookAddDto){
        Book newBook = new Book();

        newBook.setBookName(bookAddDto.getName());
        newBook.setIsAble(true);

        Book addBook = bookService.addBook(newBook);

        BookAddResponseDto bookAddResponseDto = new BookAddResponseDto();
        bookAddResponseDto.setName(addBook.getBookName());

        return new ResponseEntity(bookAddResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "책 전체 리스트", description = "DB에 저장된 전체 책 리스트를 가져옵니다.")
    @GetMapping("/list")
    public ResponseEntity bookList(){
        List<Book> bookList= bookService.getBooksList();
        BookListResponseDto bookListResponseDto = new BookListResponseDto();
        bookListResponseDto.setBookList(bookList);
        return new ResponseEntity(bookListResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "책 대여", description = "해당 책을 대여불가 상태로 DB에 저장합니다.")
    @PostMapping("/{bookId}/rent")
    public ResponseEntity bookRent(@PathVariable Long bookId, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        Book book = bookService.findById(bookId);
        if(book.getIsAble()){
            book.setRentalMemberId(loginMember.getMemberId());
            book.setIsAble(false);
            bookService.addBook(book);
            BookRentResponseDto bookRentResponseDto = new BookRentResponseDto();
            bookRentResponseDto.setMemberName(loginMember.getName());
            bookRentResponseDto.setBookName(book.getBookName());

            return new ResponseEntity(bookRentResponseDto, HttpStatus.OK);
        }
        else {
            ErrorDto errorDto = new ErrorDto();
            errorDto.setErrorMessage("대여할 수 없는 책 입니다.");
            return new ResponseEntity(errorDto ,HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "책 반납", description = "해당 책을 대여가능 상태로 DB에 저장합니다.")
    @PostMapping("/{bookId}/return")
    public ResponseEntity bookReturn(@PathVariable Long bookId, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        Book book = bookService.findById(bookId);
        if(loginMember.getMemberId() == book.getRentalMemberId()){
            book.setIsAble(true);
            bookService.addBook(book);
            BookReturnResponseDto bookReturnResponseDto = new BookReturnResponseDto();
            bookReturnResponseDto.setMemberName(loginMember.getName());
            bookReturnResponseDto.setBookName(book.getBookName());
            bookReturnResponseDto.setMessage("반납 완료");

            return new ResponseEntity(bookReturnResponseDto, HttpStatus.OK);
        }
        else {
            ErrorDto errorDto = new ErrorDto();
            errorDto.setErrorMessage("해당 책을 대여하지 않았습니다.");
            return new ResponseEntity(errorDto ,HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "책 검색", description = "검색값과 일치하는 제목의 책 리스트를 가져옵니다.")
    @GetMapping("/search")
    public ResponseEntity bookSearch(@RequestBody BookSearchDto bookSearchDto, Pageable pageable){
        pageable = PageRequest.of(bookSearchDto.getPage(), 10, Sort.by("bookId").descending());
        List<Book> bookList = bookService.getBookListByKeyword(bookSearchDto.getSearchText(), pageable.withPage(bookSearchDto.getPage()));

        BookListResponseDto bookListResponseDto = new BookListResponseDto();
        bookListResponseDto.setBookList(bookList);
        return new ResponseEntity(bookListResponseDto, HttpStatus.OK);
    }

    @PostMapping("/test")
    public String test(@RequestParam("testFile") MultipartFile mf){
        if(mf.isEmpty()){
            System.out.println("empty");
            return "empty";
        } else {
            System.out.println("notEmpty");
            return "asdfasdfaxzcv";
        }
    }

    private Member getLoginMember(Authentication authentication) {
        if(authentication == null){
            System.out.println("authentication에 아무것도 없음");
            return memberService.findById(1L);
        }
        Long memberId = (Long) authentication.getPrincipal();
        Member loginMember = memberService.findById(memberId);
        return loginMember;
    }

}
