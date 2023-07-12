package com.mb.controller;

import com.mb.domain.Book;
import com.mb.domain.Member;
import com.mb.dto.*;
import com.mb.service.BookService;
import com.mb.service.MemberService;
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

import java.util.List;

@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final MemberService memberService;

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

    @GetMapping("/list")
    public ResponseEntity bookList(){
        List<Book> bookList= bookService.getBooksList();
        BookListResponseDto bookListResponseDto = new BookListResponseDto();
        bookListResponseDto.setBookList(bookList);
        return new ResponseEntity(bookListResponseDto, HttpStatus.OK);
    }

    @PostMapping("/{bookId}/rent")
    public ResponseEntity bookRent(@PathVariable Long bookId, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        Book book = bookService.findById(bookId);
        if(book.getIsAble()){
            book.setRentalMember(loginMember);
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

    @PostMapping("/{bookId}/return")
    public ResponseEntity bookReturn(@PathVariable Long bookId, Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        Book book = bookService.findById(bookId);
        if(loginMember == book.getRentalMember()){
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

    @GetMapping("/search")
    public ResponseEntity bookSearch(@RequestBody BookSearchDto bookSearchDto, Pageable pageable){
        pageable = PageRequest.of(bookSearchDto.getPage(), 10, Sort.by("bookId").descending());
        List<Book> bookList = bookService.getBookListByKeyword(bookSearchDto.getSearchText(), pageable.withPage(bookSearchDto.getPage()));

        BookListResponseDto bookListResponseDto = new BookListResponseDto();
        bookListResponseDto.setBookList(bookList);
        return new ResponseEntity(bookListResponseDto, HttpStatus.OK);
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
